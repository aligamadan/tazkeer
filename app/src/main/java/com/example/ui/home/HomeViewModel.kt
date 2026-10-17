package com.example.ui.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.audio.AdhanAudioManager
import com.example.data.local.AppPreferences
import com.example.data.model.*
import com.example.data.prayer.PrayerCalculationEngine
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

data class HomeUiState(
    val userName: String = "",
    val cityName: String = "",
    val hijriDateText: String = "",
    val gregorianDateText: String = "",
    val greeting: String = "",
    val nextPrayer: PrayerName = PrayerName.DHUHR,
    val nextPrayerTime: LocalTime = LocalTime.NOON,
    val remainingHours: Long = 0,
    val remainingMinutes: Long = 0,
    val remainingSeconds: Long = 0,
    val progressFraction: Float = 0.5f,
    val prayerTimes: Map<PrayerName, LocalTime> = emptyMap(),
    val prayerStatuses: Map<PrayerName, PrayerStatus?> = emptyMap(),
    val notificationModes: Map<PrayerName, NotificationMode> = emptyMap(),
    val promptPrayer: PrayerName? = null,
    val midnightTime: LocalTime = LocalTime.MIDNIGHT,
    val lastThirdTime: LocalTime = LocalTime.MIDNIGHT,
    val isDst: Boolean = true
)

class HomeViewModel(
    private val appPreferences: AppPreferences,
    val audioManager: AdhanAudioManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var currentPromptShownFor: PrayerName? = null

    init {
        loadData()
        startClockTicker()
    }

    fun loadData() {
        val today = LocalDate.now()
        val lat = appPreferences.customLatitude
        val isDst = appPreferences.isDstEnabled
        val tz = if (appPreferences.selectedCityName.contains("القاهرة") || appPreferences.selectedCityName.contains("الإسكندرية")) {
            if (isDst) 3.0 else 2.0
        } else {
            appPreferences.timezoneOffset
        }
        val lng = appPreferences.customLongitude
        val method = appPreferences.calculationMethod
        val school = appPreferences.juristicSchool

        val dayTimes = PrayerCalculationEngine.calculateTimes(
            date = today,
            latitude = lat,
            longitude = lng,
            timezoneOffsetHours = tz,
            method = method,
            school = school,
            fajrAdjustmentMins = appPreferences.getPrayerAdjustment(PrayerName.FAJR),
            sunriseAdjustmentMins = appPreferences.getPrayerAdjustment(PrayerName.SUNRISE),
            dhuhrAdjustmentMins = appPreferences.getPrayerAdjustment(PrayerName.DHUHR),
            asrAdjustmentMins = appPreferences.getPrayerAdjustment(PrayerName.ASR),
            maghribAdjustmentMins = appPreferences.getPrayerAdjustment(PrayerName.MAGHRIB),
            ishaAdjustmentMins = appPreferences.getPrayerAdjustment(PrayerName.ISHA)
        )

        val timesMap = mapOf(
            PrayerName.FAJR to dayTimes.fajr,
            PrayerName.SUNRISE to dayTimes.sunrise,
            PrayerName.DHUHR to dayTimes.dhuhr,
            PrayerName.ASR to dayTimes.asr,
            PrayerName.MAGHRIB to dayTimes.maghrib,
            PrayerName.ISHA to dayTimes.isha
        )

        val statusMap = PrayerName.values().associateWith { prayer ->
            appPreferences.getPrayerStatus(today, prayer)
        }

        val notifMap = PrayerName.values().associateWith { prayer ->
            appPreferences.getNotificationMode(prayer)
        }

        val hijri = PrayerCalculationEngine.getHijriDate(today, appPreferences.hijriAdjustment)
        val hijriStr = "${hijri.day} ${hijri.monthNameArabic} ${hijri.year} هـ"

        val gregorianFormatter = DateTimeFormatter.ofPattern("EEEE، d MMMM yyyy", Locale("ar"))
        val gregorianStr = today.format(gregorianFormatter)

        val greeting = computeGreeting(appPreferences.userName)

        _uiState.value = _uiState.value.copy(
            userName = appPreferences.userName,
            cityName = appPreferences.selectedCityName,
            hijriDateText = hijriStr,
            gregorianDateText = gregorianStr,
            greeting = greeting,
            prayerTimes = timesMap,
            prayerStatuses = statusMap,
            notificationModes = notifMap,
            midnightTime = dayTimes.midnight,
            lastThirdTime = dayTimes.lastThirdOfNight,
            isDst = isDst
        )

        updateCountdown()
    }

    fun toggleDst() {
        appPreferences.isDstEnabled = !appPreferences.isDstEnabled
        loadData()
    }

    private fun startClockTicker() {
        viewModelScope.launch {
            while (true) {
                updateCountdown()
                checkDidYouPrayPrompt()
                delay(1000)
            }
        }
    }

    private fun updateCountdown() {
        val now = LocalTime.now()
        val times = _uiState.value.prayerTimes
        if (times.isEmpty()) return

        val orderedPrayers = listOf(
            PrayerName.FAJR,
            PrayerName.SUNRISE,
            PrayerName.DHUHR,
            PrayerName.ASR,
            PrayerName.MAGHRIB,
            PrayerName.ISHA
        )

        var nextP = PrayerName.FAJR
        var nextTime = times[PrayerName.FAJR] ?: LocalTime.of(5, 0)
        var prevTime = times[PrayerName.ISHA] ?: LocalTime.of(20, 0)

        var foundNext = false
        for (i in orderedPrayers.indices) {
            val p = orderedPrayers[i]
            val t = times[p] ?: continue
            if (t.isAfter(now)) {
                nextP = p
                nextTime = t
                val prevIndex = if (i > 0) i - 1 else orderedPrayers.lastIndex
                prevTime = times[orderedPrayers[prevIndex]] ?: LocalTime.of(4, 0)
                foundNext = true
                break
            }
        }

        val remainingSecs = if (foundNext) {
            Duration.between(now, nextTime).seconds
        } else {
            // Next is tomorrow's Fajr
            nextP = PrayerName.FAJR
            nextTime = times[PrayerName.FAJR] ?: LocalTime.of(5, 0)
            prevTime = times[PrayerName.ISHA] ?: LocalTime.of(20, 0)
            Duration.between(now, LocalTime.MAX).seconds + Duration.between(LocalTime.MIN, nextTime).seconds
        }

        val h = maxOf(0L, remainingSecs / 3600)
        val m = maxOf(0L, (remainingSecs % 3600) / 60)
        val s = maxOf(0L, remainingSecs % 60)

        // Compute total window for circular progress
        val totalSecsBetween = try {
            if (nextTime.isAfter(prevTime)) {
                Duration.between(prevTime, nextTime).seconds.toFloat()
            } else {
                (Duration.between(prevTime, LocalTime.MAX).seconds + Duration.between(LocalTime.MIN, nextTime).seconds).toFloat()
            }
        } catch (e: Exception) { 3600f * 3 }

        val elapsed = (totalSecsBetween - remainingSecs).coerceAtLeast(0f)
        val progress = if (totalSecsBetween > 0) (elapsed / totalSecsBetween).coerceIn(0.01f, 1f) else 0.5f

        _uiState.value = _uiState.value.copy(
            nextPrayer = nextP,
            nextPrayerTime = nextTime,
            remainingHours = h,
            remainingMinutes = m,
            remainingSeconds = s,
            progressFraction = progress
        )
    }

    private fun checkDidYouPrayPrompt() {
        val now = LocalTime.now()
        val delayMins = appPreferences.followUpDelayMinutes
        val today = LocalDate.now()
        val times = _uiState.value.prayerTimes

        val fardPrayers = listOf(
            PrayerName.ISHA,
            PrayerName.MAGHRIB,
            PrayerName.ASR,
            PrayerName.DHUHR,
            PrayerName.FAJR
        )

        for (prayer in fardPrayers) {
            val prayerTime = times[prayer] ?: continue
            val promptTime = prayerTime.plusMinutes(delayMins.toLong())
            if (now.isAfter(promptTime) && Duration.between(promptTime, now).toMinutes() < 120) {
                val currentStatus = appPreferences.getPrayerStatus(today, prayer)
                if (currentStatus == null && currentPromptShownFor != prayer) {
                    currentPromptShownFor = prayer
                    _uiState.value = _uiState.value.copy(promptPrayer = prayer)
                    break
                }
            }
        }
    }

    fun dismissPrompt() {
        _uiState.value = _uiState.value.copy(promptPrayer = null)
    }

    fun recordPrayerStatus(prayer: PrayerName, status: PrayerStatus) {
        val today = LocalDate.now()
        appPreferences.recordPrayerStatus(today, prayer, status)
        val updatedStatuses = _uiState.value.prayerStatuses.toMutableMap()
        updatedStatuses[prayer] = status
        _uiState.value = _uiState.value.copy(prayerStatuses = updatedStatuses)
    }

    fun toggleNotificationMode(prayer: PrayerName) {
        val current = appPreferences.getNotificationMode(prayer)
        val next = when (current) {
            NotificationMode.ADHAN -> NotificationMode.NOTIFICATION
            NotificationMode.NOTIFICATION -> NotificationMode.OFF
            NotificationMode.OFF -> NotificationMode.ADHAN
        }
        appPreferences.setNotificationMode(prayer, next)
        val updatedModes = _uiState.value.notificationModes.toMutableMap()
        updatedModes[prayer] = next
        _uiState.value = _uiState.value.copy(notificationModes = updatedModes)
    }

    private fun computeGreeting(name: String): String {
        val hour = LocalTime.now().hour
        val namePart = if (name.isNotBlank()) " يا $name" else ""
        return when (hour) {
            in 4..11 -> "صباح الخير والبركة$namePart ☀️"
            in 12..16 -> "السلام عليكم ورحمة الله$namePart 🌿"
            in 17..22 -> "مساء الخير والسكينة$namePart 🌙"
            else -> "طاب ليلك بذكر الله$namePart ✨"
        }
    }
}

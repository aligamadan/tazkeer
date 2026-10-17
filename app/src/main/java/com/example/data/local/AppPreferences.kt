package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.*
import com.example.data.prayer.CityData
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AppPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("tazkeer_app_prefs", Context.MODE_PRIVATE)

    var isFirstLaunch: Boolean
        get() = prefs.getBoolean("is_first_launch", true)
        set(value) = prefs.edit().putBoolean("is_first_launch", value).apply()

    var userName: String
        get() = prefs.getString("user_name", "") ?: ""
        set(value) = prefs.edit().putString("user_name", value).apply()

    var selectedCityName: String
        get() = prefs.getString("selected_city", CityData.DEFAULT_CITY.nameArabic) ?: CityData.DEFAULT_CITY.nameArabic
        set(value) = prefs.edit().putString("selected_city", value).apply()

    var customLatitude: Double
        get() = prefs.getString("custom_lat", CityData.DEFAULT_CITY.latitude.toString())?.toDoubleOrNull()
            ?: CityData.DEFAULT_CITY.latitude
        set(value) = prefs.edit().putString("custom_lat", value.toString()).apply()

    var customLongitude: Double
        get() = prefs.getString("custom_lng", CityData.DEFAULT_CITY.longitude.toString())?.toDoubleOrNull()
            ?: CityData.DEFAULT_CITY.longitude
        set(value) = prefs.edit().putString("custom_lng", value.toString()).apply()

    var timezoneOffset: Double
        get() = prefs.getString("timezone_offset", CityData.DEFAULT_CITY.timezoneOffsetHours.toString())?.toDoubleOrNull()
            ?: CityData.DEFAULT_CITY.timezoneOffsetHours
        set(value) = prefs.edit().putString("timezone_offset", value.toString()).apply()

    var calculationMethod: CalculationMethod
        get() {
            val name = prefs.getString("calc_method", CalculationMethod.EGYPTIAN.name)
            return try { CalculationMethod.valueOf(name ?: CalculationMethod.EGYPTIAN.name) }
            catch (e: Exception) { CalculationMethod.EGYPTIAN }
        }
        set(value) = prefs.edit().putString("calc_method", value.name).apply()

    var juristicSchool: JuristicSchool
        get() {
            val name = prefs.getString("juristic_school", JuristicSchool.SHAFI.name)
            return try { JuristicSchool.valueOf(name ?: JuristicSchool.SHAFI.name) }
            catch (e: Exception) { JuristicSchool.SHAFI }
        }
        set(value) = prefs.edit().putString("juristic_school", value.name).apply()

    var adhanMuezzin: AdhanMuezzin
        get() {
            val id = prefs.getString("adhan_muezzin", AdhanMuezzin.ALAFASY.name)
            return try { AdhanMuezzin.valueOf(id ?: AdhanMuezzin.ALAFASY.name) }
            catch (e: Exception) { AdhanMuezzin.ALAFASY }
        }
        set(value) = prefs.edit().putString("adhan_muezzin", value.name).apply()

    var followUpDelayMinutes: Int
        get() = prefs.getInt("followup_delay_mins", 10)
        set(value) = prefs.edit().putInt("followup_delay_mins", value).apply()

    var hijriAdjustment: Int
        get() = prefs.getInt("hijri_adjustment", 0)
        set(value) = prefs.edit().putInt("hijri_adjustment", value).apply()

    var isDstEnabled: Boolean
        get() = prefs.getBoolean("is_dst_enabled", true)
        set(value) = prefs.edit().putBoolean("is_dst_enabled", value).apply()

    var themeMode: String // "AUTO", "LIGHT", "DARK"
        get() = prefs.getString("theme_mode", "LIGHT") ?: "LIGHT"
        set(value) = prefs.edit().putString("theme_mode", value).apply()

    fun getNotificationMode(prayer: PrayerName): NotificationMode {
        val name = prefs.getString("notif_mode_${prayer.name}", NotificationMode.ADHAN.name)
        return try { NotificationMode.valueOf(name ?: NotificationMode.ADHAN.name) }
        catch (e: Exception) { NotificationMode.ADHAN }
    }

    fun setNotificationMode(prayer: PrayerName, mode: NotificationMode) {
        prefs.edit().putString("notif_mode_${prayer.name}", mode.name).apply()
    }

    // Per-prayer minute fine adjustments
    fun getPrayerAdjustment(prayer: PrayerName): Int {
        return prefs.getInt("adj_${prayer.name}", 0)
    }

    fun setPrayerAdjustment(prayer: PrayerName, minutes: Int) {
        prefs.edit().putInt("adj_${prayer.name}", minutes).apply()
    }

    // Save prayer logging answer
    fun recordPrayerStatus(date: LocalDate, prayer: PrayerName, status: PrayerStatus) {
        val key = "prayer_status_${date.format(DateTimeFormatter.ISO_LOCAL_DATE)}_${prayer.name}"
        prefs.edit().putString(key, status.name).apply()
    }

    fun getPrayerStatus(date: LocalDate, prayer: PrayerName): PrayerStatus? {
        val key = "prayer_status_${date.format(DateTimeFormatter.ISO_LOCAL_DATE)}_${prayer.name}"
        val value = prefs.getString(key, null) ?: return null
        return try { PrayerStatus.valueOf(value) } catch (e: Exception) { null }
    }

    // Streak & Statistics calculation
    fun getCompletedPrayersCount(date: LocalDate): Int {
        var count = 0
        PrayerName.values().filter { it.isFard }.forEach { p ->
            val status = getPrayerStatus(date, p)
            if (status == PrayerStatus.ON_TIME || status == PrayerStatus.IN_MOSQUE || status == PrayerStatus.LATE) {
                count++
            }
        }
        return count
    }

    fun calculateCurrentStreak(): Int {
        var streak = 0
        var checkDate = LocalDate.now()
        // Check today first, if not finished yet check yesterday backwards
        val todayCount = getCompletedPrayersCount(checkDate)
        if (todayCount >= 3) {
            streak++
        }
        checkDate = checkDate.minusDays(1)
        while (streak < 365) {
            val count = getCompletedPrayersCount(checkDate)
            if (count >= 4) { // Active worshipper threshold
                streak++
                checkDate = checkDate.minusDays(1)
            } else {
                break
            }
        }
        return maxOf(streak, 1) // default 1 for encouragement
    }

    fun getMosquePrayersCount(): Int {
        var mosqueCount = 0
        for (i in 0..30) {
            val d = LocalDate.now().minusDays(i.toLong())
            PrayerName.values().forEach { p ->
                if (getPrayerStatus(d, p) == PrayerStatus.IN_MOSQUE) {
                    mosqueCount++
                }
            }
        }
        return mosqueCount
    }
}

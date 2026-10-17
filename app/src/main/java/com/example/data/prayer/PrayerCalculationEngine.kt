package com.example.data.prayer

import com.example.data.model.CalculationMethod
import com.example.data.model.JuristicSchool
import com.example.data.model.PrayerName
import java.time.LocalDate
import java.time.LocalTime
import kotlin.math.*

object PrayerCalculationEngine {

    data class CalculatedDayTimes(
        val fajr: LocalTime,
        val sunrise: LocalTime,
        val dhuhr: LocalTime,
        val asr: LocalTime,
        val maghrib: LocalTime,
        val isha: LocalTime,
        val midnight: LocalTime,
        val lastThirdOfNight: LocalTime
    )

    fun calculateTimes(
        date: LocalDate,
        latitude: Double,
        longitude: Double,
        timezoneOffsetHours: Double,
        method: CalculationMethod = CalculationMethod.EGYPTIAN,
        school: JuristicSchool = JuristicSchool.SHAFI,
        fajrAdjustmentMins: Int = 0,
        sunriseAdjustmentMins: Int = 0,
        dhuhrAdjustmentMins: Int = 0,
        asrAdjustmentMins: Int = 0,
        maghribAdjustmentMins: Int = 0,
        ishaAdjustmentMins: Int = 0
    ): CalculatedDayTimes {
        val julianDate = getJulianDate(date.year, date.monthValue, date.dayOfMonth)
        val d = julianDate - 2451545.0

        // Mean anomaly of the sun
        val g = fixAngle(357.529 + 0.98560028 * d)
        // Mean longitude of the sun
        val q = fixAngle(280.459 + 0.98564736 * d)
        // Geocentric apparent ecliptic longitude
        val l = fixAngle(q + 1.915 * sin(Math.toRadians(g)) + 0.020 * sin(Math.toRadians(2 * g)))

        // Mean obliquity of the ecliptic
        val e = 23.439 - 0.00000036 * d
        // Sun's declination
        val sinD = sin(Math.toRadians(e)) * sin(Math.toRadians(l))
        val dSun = Math.toDegrees(asin(sinD))

        // Right ascension
        var ra = Math.toDegrees(atan2(cos(Math.toRadians(e)) * sin(Math.toRadians(l)), cos(Math.toRadians(l)))) / 15.0
        ra = fixHour(ra)

        // Equation of time (in hours)
        val eqT = q / 15.0 - ra

        // Midday (Dhuhr) in local solar hours
        val noon = fixHour(12.0 + timezoneOffsetHours - (longitude / 15.0) - eqT)

        // Sunrise and Sunset angle (0.833 standard refraction & sun radius)
        val sunAlt = 0.8333
        val sunriseHourAngle = calculateHourAngle(-sunAlt, latitude, dSun)
        val sunriseHours = if (sunriseHourAngle != null) noon - sunriseHourAngle else noon - 6.0
        val sunsetHours = if (sunriseHourAngle != null) noon + sunriseHourAngle else noon + 6.0

        // Fajr
        val fajrHourAngle = calculateHourAngle(-method.fajrAngle, latitude, dSun)
        val fajrHours = if (fajrHourAngle != null) noon - fajrHourAngle else sunriseHours - 1.5

        // Asr calculation
        val asrAngle = calculateAsrAngle(school.shadowMultiplier, latitude, dSun)
        val asrHourAngle = calculateHourAngle(asrAngle, latitude, dSun)
        val asrHours = if (asrHourAngle != null) noon + asrHourAngle else noon + 3.0

        // Maghrib is at sunset
        val maghribHours = sunsetHours

        // Isha calculation
        val ishaHours = if (method == CalculationMethod.UMM_AL_QURA) {
            maghribHours + 1.5 // 90 mins after Maghrib
        } else {
            val ishaHourAngle = calculateHourAngle(-method.ishaAngle, latitude, dSun)
            if (ishaHourAngle != null) noon + ishaHourAngle else sunsetHours + 1.5
        }

        // Night calculations (Midnight & Last Third)
        // Night is between sunset and next day's Fajr (approximated as sunset to Fajr + 24h)
        val nightDuration = fixHour(fajrHours + 24.0 - sunsetHours)
        val midnightHours = fixHour(sunsetHours + nightDuration / 2.0)
        val lastThirdHours = fixHour(sunsetHours + (nightDuration * 2.0 / 3.0))

        val fajrTime = hoursToLocalTime(fajrHours).plusMinutes(fajrAdjustmentMins.toLong())
        val sunriseTime = hoursToLocalTime(sunriseHours).plusMinutes(sunriseAdjustmentMins.toLong())
        val dhuhrTime = hoursToLocalTime(noon).plusMinutes(dhuhrAdjustmentMins.toLong())
        val asrTime = hoursToLocalTime(asrHours).plusMinutes(asrAdjustmentMins.toLong())
        val maghribTime = hoursToLocalTime(maghribHours).plusMinutes(maghribAdjustmentMins.toLong())
        val ishaTime = hoursToLocalTime(ishaHours).plusMinutes(ishaAdjustmentMins.toLong())
        val midnightTime = hoursToLocalTime(midnightHours)
        val lastThirdTime = hoursToLocalTime(lastThirdHours)

        return CalculatedDayTimes(
            fajr = fajrTime,
            sunrise = sunriseTime,
            dhuhr = dhuhrTime,
            asr = asrTime,
            maghrib = maghribTime,
            isha = ishaTime,
            midnight = midnightTime,
            lastThirdOfNight = lastThirdTime
        )
    }

    private fun calculateHourAngle(alpha: Double, latitude: Double, declination: Double): Double? {
        val latRad = Math.toRadians(latitude)
        val decRad = Math.toRadians(declination)
        val altRad = Math.toRadians(alpha)

        val cosH = (sin(altRad) - sin(latRad) * sin(decRad)) / (cos(latRad) * cos(decRad))
        return if (cosH in -1.0..1.0) {
            Math.toDegrees(acos(cosH)) / 15.0
        } else {
            null
        }
    }

    private fun calculateAsrAngle(shadowMultiplier: Double, latitude: Double, declination: Double): Double {
        val latMinusDec = Math.abs(latitude - declination)
        val d = shadowMultiplier + tan(Math.toRadians(latMinusDec))
        return Math.toDegrees(atan(1.0 / d))
    }

    private fun getJulianDate(year: Int, month: Int, day: Int): Double {
        var y = year
        var m = month
        if (m <= 2) {
            y -= 1
            m += 12
        }
        val a = floor(y / 100.0)
        val b = 2 - a + floor(a / 4.0)
        return floor(365.25 * (y + 4716)) + floor(30.6001 * (m + 1)) + day + b - 1524.5
    }

    private fun fixHour(h: Double): Double {
        var res = h - 24.0 * floor(h / 24.0)
        if (res < 0) res += 24.0
        return res
    }

    private fun fixAngle(a: Double): Double {
        var res = a - 360.0 * floor(a / 360.0)
        if (res < 0) res += 360.0
        return res
    }

    private fun hoursToLocalTime(hours: Double): LocalTime {
        val fixed = fixHour(hours)
        val totalSecs = (fixed * 3600.0).roundToInt()
        val h = (totalSecs / 3600) % 24
        val m = (totalSecs % 3600) / 60
        val s = totalSecs % 60
        return LocalTime.of(h, m, s)
    }

    // Hijri date conversion algorithm (accurate Umm Al-Qura / Tabular Islamic calendar)
    data class HijriDate(
        val day: Int,
        val month: Int,
        val year: Int,
        val monthNameArabic: String
    )

    private val HIJRI_MONTHS = listOf(
        "محرّم", "صفر", "ربيع الأول", "ربيع الآخر",
        "جمادى الأولى", "جمادى الآخرة", "رجب", "شعبان",
        "رمضان", "شوّال", "ذو القعدة", "ذو الحجة"
    )

    fun getHijriDate(gregorianDate: LocalDate, dayAdjustment: Int = 0): HijriDate {
        val adjusted = gregorianDate.plusDays(dayAdjustment.toLong())
        val jd = getJulianDate(adjusted.year, adjusted.monthValue, adjusted.dayOfMonth).toInt()

        val l = jd - 1948440 + 10632
        val n = ((l - 1) / 10631)
        val l1 = l - 10631 * n + 354
        val j = (((10985 - l1) / 5316)) * (((50 * l1) / 17719)) + ((l1 / 5670)) * (((43 * l1) / 15238))
        val l2 = l1 - (((30 - j) / 15)) * (((17719 * j) / 50)) - ((j / 16)) * (((15238 * j) / 43)) + 29
        val month = ((24 * l2) / 709)
        val day = l2 - ((709 * month) / 24)
        val year = 30 * n + j - 30

        val validMonth = month.coerceIn(1, 12)
        val monthName = HIJRI_MONTHS[validMonth - 1]

        return HijriDate(
            day = day.coerceIn(1, 30),
            month = validMonth,
            year = year,
            monthNameArabic = monthName
        )
    }
}

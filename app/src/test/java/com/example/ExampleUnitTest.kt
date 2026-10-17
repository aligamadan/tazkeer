package com.example

import com.example.data.model.CalculationMethod
import com.example.data.model.JuristicSchool
import com.example.data.prayer.PrayerCalculationEngine
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate

class ExampleUnitTest {
    @Test
    fun testPrayerCalculationEngine_Cairo() {
        val date = LocalDate.of(2026, 10, 17)
        val times = PrayerCalculationEngine.calculateTimes(
            date = date,
            latitude = 30.0444,
            longitude = 31.2357,
            timezoneOffsetHours = 2.0,
            method = CalculationMethod.EGYPTIAN,
            school = JuristicSchool.SHAFI
        )

        assertNotNull(times.fajr)
        assertNotNull(times.sunrise)
        assertNotNull(times.dhuhr)
        assertNotNull(times.asr)
        assertNotNull(times.maghrib)
        assertNotNull(times.isha)

        assertTrue("Fajr must be before Sunrise", times.fajr.isBefore(times.sunrise))
        assertTrue("Sunrise must be before Dhuhr", times.sunrise.isBefore(times.dhuhr))
        assertTrue("Dhuhr must be before Asr", times.dhuhr.isBefore(times.asr))
        assertTrue("Asr must be before Maghrib", times.asr.isBefore(times.maghrib))
        assertTrue("Maghrib must be before Isha", times.maghrib.isBefore(times.isha))
    }

    @Test
    fun testHijriDateConversion() {
        val date = LocalDate.of(2026, 10, 17)
        val hijri = PrayerCalculationEngine.getHijriDate(date)
        assertTrue("Hijri month must be 1..12", hijri.month in 1..12)
        assertTrue("Hijri day must be 1..30", hijri.day in 1..30)
        assertTrue("Hijri year must be around 1448", hijri.year in 1440..1460)
        assertTrue("Month name must not be empty", hijri.monthNameArabic.isNotEmpty())
    }
}

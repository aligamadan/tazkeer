package com.example.data.model

import java.time.LocalDate
import java.time.LocalTime

enum class PrayerName(val arabicName: String, val englishName: String) {
    FAJR("الفجر", "Fajr"),
    SUNRISE("الشروق", "Sunrise"),
    DHUHR("الظهر", "Dhuhr"),
    ASR("العصر", "Asr"),
    MAGHRIB("المغرب", "Maghrib"),
    ISHA("العشاء", "Isha");

    val isFard: Boolean
        get() = this != SUNRISE
}

enum class PrayerStatus(val titleArabic: String, val icon: String) {
    ON_TIME("صليت في الوقت", "✅"),
    IN_MOSQUE("صليت في المسجد", "🕌"),
    LATE("صليت متأخرًا", "⏰"),
    NOT_YET("لم أصلِّ بعد", "❌"),
    REMIND_LATER("ذكّرني لاحقًا", "⏳"),
    MISSED("فاتتني", "⚠️")
}

data class SinglePrayerTime(
    val prayer: PrayerName,
    val time: LocalTime,
    val notificationMode: NotificationMode = NotificationMode.ADHAN
)

enum class NotificationMode(val labelArabic: String) {
    ADHAN("الأذان"),
    NOTIFICATION("إشعار صامت"),
    OFF("إيقاف")
}

enum class CalculationMethod(
    val id: Int,
    val titleArabic: String,
    val fajrAngle: Double,
    val ishaAngle: Double
) {
    EGYPTIAN(5, "الهيئة المصرية العامة للمساحة", 19.5, 17.5),
    UMM_AL_QURA(4, "أم القرى - مكة المكرمة", 18.5, 19.0), // 90 min after Maghrib (120 in Ramadan)
    MUSLIM_WORLD_LEAGUE(3, "رابطة العالم الإسلامي", 18.0, 17.0),
    ISNA(2, "الجمعية الإسلامية لأمريكا الشمالية (ISNA)", 15.0, 15.0),
    KARACHI(1, "جامعة العلوم الإسلامية بكراتشي", 18.0, 18.0),
    DUBAI(16, "دائرة الشؤون الإسلامية بدبي", 18.2, 18.2),
    KUWAIT(9, "دولة الكويت", 18.0, 17.5)
}

enum class JuristicSchool(val titleArabic: String, val shadowMultiplier: Double) {
    SHAFI("جمهور الفقهاء (شافعي، مالكي، حنبلي)", 1.0),
    HANAFI("المذهب الحنفي", 2.0)
}

enum class AdhanMuezzin(
    val id: String,
    val nameArabic: String,
    val reciterName: String,
    val previewUrl: String
) {
    ALAFASY(
        "alafasy",
        "الشيخ مشاري راشد العفاسي",
        "العفاسي",
        "https://cdn.islamic.network/prayer-times/audio/adhan/alafasy.mp3"
    ),
    MAKKAH(
        "makkah",
        "أذان الحرم المكي الشريف",
        "مكة المكرمة",
        "https://cdn.islamic.network/prayer-times/audio/adhan/makkah.mp3"
    ),
    MADINAH(
        "madinah",
        "أذان الحرم النبوي الشريف",
        "المدينة المنورة",
        "https://cdn.islamic.network/prayer-times/audio/adhan/madinah.mp3"
    ),
    AL_AQSA(
        "al_aqsa",
        "أذان المسجد الأقصى المبارك",
        "القدس الشريف",
        "https://cdn.islamic.network/prayer-times/audio/adhan/al-aqsa.mp3"
    ),
    ABDULBASIT(
        "abdulbasit",
        "الشيخ عبد الباسط عبد الصمد",
        "عبد الباسط",
        "https://cdn.islamic.network/prayer-times/audio/adhan/abdulbasit.mp3"
    ),
    SHORT_TAKBIR(
        "takbir",
        "تكبيرات مختصرة",
        "تكبير",
        "https://cdn.islamic.network/prayer-times/audio/adhan/short-takbir.mp3"
    ),
    SOFT_TONE(
        "soft_tone",
        "نغمة هادئة تنبيهية",
        "تنبيه هادئ",
        ""
    )
}

data class PrayerDailyRecord(
    val date: LocalDate,
    val prayerStatusMap: Map<PrayerName, PrayerStatus> = emptyMap()
)

data class CityLocation(
    val nameArabic: String,
    val nameEnglish: String,
    val countryArabic: String,
    val countryEnglish: String,
    val latitude: Double,
    val longitude: Double,
    val timezoneOffsetHours: Double
)

package com.example.data.prayer

import com.example.data.model.CityLocation

object CityData {
    val CITIES = listOf(
        CityLocation("القاهرة", "Cairo", "مصر", "Egypt", 30.0444, 31.2357, 3.0),
        CityLocation("الإسكندرية", "Alexandria", "مصر", "Egypt", 31.2001, 29.9187, 3.0),
        CityLocation("مكة المكرمة", "Makkah", "المملكة العربية السعودية", "Saudi Arabia", 21.4225, 39.8262, 3.0),
        CityLocation("المدينة المنورة", "Madinah", "المملكة العربية السعودية", "Saudi Arabia", 24.5247, 39.5692, 3.0),
        CityLocation("الرياض", "Riyadh", "المملكة العربية السعودية", "Saudi Arabia", 24.7136, 46.6753, 3.0),
        CityLocation("جدة", "Jeddah", "المملكة العربية السعودية", "Saudi Arabia", 21.5433, 39.1728, 3.0),
        CityLocation("القدس الشريف", "Jerusalem", "فلسطين", "Palestine", 31.7683, 35.2137, 3.0),
        CityLocation("عمّان", "Amman", "الأردن", "Jordan", 31.9454, 35.9284, 3.0),
        CityLocation("دبي", "Dubai", "الإمارات العربية المتحدة", "United Arab Emirates", 25.2048, 55.2708, 4.0),
        CityLocation("أبو ظبي", "Abu Dhabi", "الإمارات العربية المتحدة", "United Arab Emirates", 24.4539, 54.3773, 4.0),
        CityLocation("الدوحة", "Doha", "قطر", "Qatar", 25.2854, 51.5310, 3.0),
        CityLocation("الكويت", "Kuwait City", "الكويت", "Kuwait", 29.3759, 47.9774, 3.0),
        CityLocation("المنامة", "Manama", "البحرين", "Bahrain", 26.2285, 50.5860, 3.0),
        CityLocation("مسقط", "Muscat", "عُمان", "Oman", 23.5880, 58.3829, 4.0),
        CityLocation("بغداد", "Baghdad", "العراق", "Iraq", 33.3152, 44.3661, 3.0),
        CityLocation("دمشق", "Damascus", "سوريا", "Syria", 33.5138, 36.2765, 3.0),
        CityLocation("بيروت", "Beirut", "لبنان", "Lebanon", 33.8938, 35.5018, 3.0),
        CityLocation("طرابلس", "Tripoli", "ليبيا", "Libya", 32.8872, 13.1913, 2.0),
        CityLocation("تونس", "Tunis", "تونس", "Tunisia", 36.8065, 10.1815, 1.0),
        CityLocation("الجزائر", "Algiers", "الجزائر", "Algeria", 36.7538, 3.0588, 1.0),
        CityLocation("الرباط", "Rabat", "المغرب", "Morocco", 34.0209, -6.8416, 1.0),
        CityLocation("الدار البيضاء", "Casablanca", "المغرب", "Morocco", 33.5731, -7.5898, 1.0),
        CityLocation("الخرطوم", "Khartoum", "السودان", "Sudan", 15.5007, 32.5599, 2.0),
        CityLocation("صنعاء", "Sanaa", "اليمن", "Yemen", 15.3694, 44.1910, 3.0),
        CityLocation("إسطنبول", "Istanbul", "تركيا", "Turkey", 41.0082, 28.9784, 3.0),
        CityLocation("لندن", "London", "المملكة المتحدة", "United Kingdom", 51.5074, -0.1278, 1.0),
        CityLocation("باريس", "Paris", "فرنسا", "France", 48.8566, 2.3522, 2.0),
        CityLocation("برلين", "Berlin", "ألمانيا", "Germany", 52.5200, 13.4050, 2.0),
        CityLocation("نيويورك", "New York", "الولايات المتحدة", "United States", 40.7128, -74.0060, -4.0),
        CityLocation("تورونتو", "Toronto", "كندا", "Canada", 43.6532, -79.3832, -4.0),
        CityLocation("كوالالمبور", "Kuala Lumpur", "ماليزيا", "Malaysia", 3.1390, 101.6869, 8.0),
        CityLocation("جاكرتا", "Jakarta", "إندونيسيا", "Indonesia", -6.2088, 106.8456, 7.0),
        CityLocation("إسلام أباد", "Islamabad", "باكستان", "Pakistan", 33.6844, 73.0479, 5.0)
    )

    val DEFAULT_CITY = CITIES[0] // Cairo
}

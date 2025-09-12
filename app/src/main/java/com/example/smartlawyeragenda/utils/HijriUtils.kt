package com.example.smartlawyeragenda.utils

import java.util.*

object HijriUtils {

    // Simple Hijri date conversion (approximate)
    // For production, consider using a proper Islamic calendar library
    private const val HIJRI_EPOCH = 227015L // Approximate epoch for Hijri calendar
    private const val HIJRI_YEAR_LENGTH = 354.367056 // Average Hijri year length in days

    fun getHijriDate(gregorianDate: Date): String {
        val calendar = Calendar.getInstance()
        calendar.time = gregorianDate

        val gregorianEpoch = calendar.timeInMillis / (1000 * 60 * 60 * 24)
        val hijriEpoch = gregorianEpoch - HIJRI_EPOCH
        val hijriYear = (hijriEpoch / HIJRI_YEAR_LENGTH).toInt() + 1

        // Calculate month and day (simplified)
        val daysInYear = (hijriEpoch % HIJRI_YEAR_LENGTH).toInt()
        val hijriMonth = (daysInYear / 29.5).toInt() + 1
        val hijriDay = (daysInYear % 29.5).toInt() + 1

        val monthNames = arrayOf(
            "محرم", "صفر", "ربيع الأول", "ربيع الثاني", "جمادى الأولى", "جمادى الثانية",
            "رجب", "شعبان", "رمضان", "شوال", "ذو القعدة", "ذو الحجة"
        )

        val safeMonthIndex = when {
            hijriMonth < 1 -> 0
            hijriMonth > 12 -> 11
            else -> hijriMonth - 1
        }

        return "$hijriDay ${monthNames[safeMonthIndex]} $hijriYear هـ"
    }

    fun getHijriDateShort(gregorianDate: Date): String {
        val calendar = Calendar.getInstance()
        calendar.time = gregorianDate

        val gregorianEpoch = calendar.timeInMillis / (1000 * 60 * 60 * 24)
        val hijriEpoch = gregorianEpoch - HIJRI_EPOCH
        val hijriYear = (hijriEpoch / HIJRI_YEAR_LENGTH).toInt() + 1

        val daysInYear = (hijriEpoch % HIJRI_YEAR_LENGTH).toInt()
        val hijriMonth = (daysInYear / 29.5).toInt() + 1
        val hijriDay = (daysInYear % 29.5).toInt() + 1

        val safeMonth = if (hijriMonth in 1..12) hijriMonth else 1

        return String.format(Locale.getDefault(), "%02d/%02d/%04d", hijriDay, safeMonth, hijriYear)
    }

    fun getTodayHijri(): String {
        return getHijriDate(Date())
    }

    fun getTodayHijriShort(): String {
        return getHijriDateShort(Date())
    }
}

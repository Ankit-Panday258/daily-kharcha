package com.dailykharcha.app.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Utility for date formatting with bilingual (Hindi/English) friendly relative time markers.
 */
object DateFormatter {

    private val displayDateFormat = SimpleDateFormat("dd MMM, yyyy", Locale.ENGLISH)
    private val shortDateFormat = SimpleDateFormat("dd MMM", Locale.ENGLISH)
    private val monthYearFormat = SimpleDateFormat("MMMM yyyy", Locale.ENGLISH)
    private val shortMonthFormat = SimpleDateFormat("MMM", Locale.ENGLISH)

    fun formatDate(epochMillis: Long): String {
        return displayDateFormat.format(Date(epochMillis))
    }

    fun formatShortDate(epochMillis: Long): String {
        return shortDateFormat.format(Date(epochMillis))
    }

    /**
     * Formats date with Hinglish relative indicators:
     * - "Aaj (Today), 04 Nov"
     * - "Kal (Yesterday), 03 Nov"
     * - "01 Nov, 2026"
     */
    fun formatFriendlyDate(epochMillis: Long): String {
        val targetCalendar = Calendar.getInstance().apply { timeInMillis = epochMillis }
        val todayCalendar = Calendar.getInstance()

        val isToday = isSameDay(targetCalendar, todayCalendar)
        if (isToday) {
            return "Aaj (${shortDateFormat.format(Date(epochMillis))})"
        }

        val yesterdayCalendar = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -1)
        }
        val isYesterday = isSameDay(targetCalendar, yesterdayCalendar)
        if (isYesterday) {
            return "Kal (${shortDateFormat.format(Date(epochMillis))})"
        }

        return displayDateFormat.format(Date(epochMillis))
    }

    fun formatMonthYear(year: Int, month: Int): String {
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1)
            set(Calendar.DAY_OF_MONTH, 1)
        }
        return monthYearFormat.format(cal.time)
    }

    fun formatShortMonth(month: Int): String {
        val cal = Calendar.getInstance().apply {
            set(Calendar.MONTH, month - 1)
            set(Calendar.DAY_OF_MONTH, 1)
        }
        return shortMonthFormat.format(cal.time)
    }

    fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    fun getStartOfDay(epochMillis: Long): Long {
        val cal = Calendar.getInstance().apply {
            timeInMillis = epochMillis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }
}

package com.dailykharcha.app.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class DateFormatterTest {

    @Test
    fun testFormatMonthYear() {
        val formatted = DateFormatter.formatMonthYear(2026, 11)
        assertEquals("November 2026", formatted)

        val janFormatted = DateFormatter.formatMonthYear(2027, 1)
        assertEquals("January 2027", janFormatted)
    }

    @Test
    fun testFormatShortMonth() {
        assertEquals("Jan", DateFormatter.formatShortMonth(1))
        assertEquals("Nov", DateFormatter.formatShortMonth(11))
        assertEquals("Dec", DateFormatter.formatShortMonth(12))
    }

    @Test
    fun testFormatFriendlyDate_today() {
        val todayMillis = System.currentTimeMillis()
        val friendly = DateFormatter.formatFriendlyDate(todayMillis)
        assertTrue(friendly.startsWith("Aaj"))
    }

    @Test
    fun testFormatFriendlyDate_yesterday() {
        val cal = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -1)
        }
        val friendly = DateFormatter.formatFriendlyDate(cal.timeInMillis)
        assertTrue(friendly.startsWith("Kal"))
    }

    @Test
    fun testGetStartOfDay() {
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 15)
            set(Calendar.MINUTE, 30)
            set(Calendar.SECOND, 45)
        }
        val startOfDay = DateFormatter.getStartOfDay(cal.timeInMillis)
        val verifyCal = Calendar.getInstance().apply { timeInMillis = startOfDay }
        assertEquals(0, verifyCal.get(Calendar.HOUR_OF_DAY))
        assertEquals(0, verifyCal.get(Calendar.MINUTE))
        assertEquals(0, verifyCal.get(Calendar.SECOND))
        assertEquals(0, verifyCal.get(Calendar.MILLISECOND))
    }
}

package com.dailykharcha.app.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CurrencyFormatterTest {

    @Test
    fun testParseRupeesToPaise_validWholeNumbers() {
        assertEquals(100L, CurrencyFormatter.parseRupeesToPaise("1"))
        assertEquals(5000L, CurrencyFormatter.parseRupeesToPaise("50"))
        assertEquals(150000L, CurrencyFormatter.parseRupeesToPaise("1500"))
        assertEquals(10000000L, CurrencyFormatter.parseRupeesToPaise("100000"))
    }

    @Test
    fun testParseRupeesToPaise_validDecimals() {
        assertEquals(15050L, CurrencyFormatter.parseRupeesToPaise("150.50"))
        assertEquals(1075L, CurrencyFormatter.parseRupeesToPaise("10.75"))
        assertEquals(99L, CurrencyFormatter.parseRupeesToPaise("0.99"))
        assertEquals(1005L, CurrencyFormatter.parseRupeesToPaise("10.05"))
    }

    @Test
    fun testParseRupeesToPaise_withSymbolsAndCommas() {
        assertEquals(150000L, CurrencyFormatter.parseRupeesToPaise("₹ 1,500"))
        assertEquals(125050L, CurrencyFormatter.parseRupeesToPaise(" ₹1,250.50 "))
        assertEquals(10000000L, CurrencyFormatter.parseRupeesToPaise("1,00,000"))
    }

    @Test
    fun testParseRupeesToPaise_invalidInputs() {
        assertNull(CurrencyFormatter.parseRupeesToPaise(""))
        assertNull(CurrencyFormatter.parseRupeesToPaise("   "))
        assertNull(CurrencyFormatter.parseRupeesToPaise("abc"))
        assertNull(CurrencyFormatter.parseRupeesToPaise("0"))
        assertNull(CurrencyFormatter.parseRupeesToPaise("-50"))
        assertNull(CurrencyFormatter.parseRupeesToPaise("-100.50"))
    }

    @Test
    fun testIsValidAmount() {
        assertTrue(CurrencyFormatter.isValidAmount("10"))
        assertTrue(CurrencyFormatter.isValidAmount("10.50"))
        assertFalse(CurrencyFormatter.isValidAmount("0"))
        assertFalse(CurrencyFormatter.isValidAmount("-10"))
        assertFalse(CurrencyFormatter.isValidAmount("xyz"))
    }

    @Test
    fun testFormatPaiseForInput() {
        assertEquals("50", CurrencyFormatter.formatPaiseForInput(5000L))
        assertEquals("150.50", CurrencyFormatter.formatPaiseForInput(15050L))
        assertEquals("0.99", CurrencyFormatter.formatPaiseForInput(99L))
    }

    @Test
    fun testFormatPaiseToRupees_containsRupeeSymbol() {
        val result = CurrencyFormatter.formatPaiseToRupees(5000L)
        assertTrue(result.contains("₹"))
        assertTrue(result.contains("50"))

        val decimalResult = CurrencyFormatter.formatPaiseToRupees(15050L)
        assertTrue(decimalResult.contains("₹"))
        assertTrue(decimalResult.contains("150.50"))
    }
}

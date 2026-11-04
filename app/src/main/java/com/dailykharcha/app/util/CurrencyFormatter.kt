package com.dailykharcha.app.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

/**
 * Utility for Indian Rupee (₹) currency formatting and integer paise conversions.
 *
 * All amounts are stored internally as 64-bit integer paise (1 Rupee = 100 Paise)
 * to ensure 100% precision with financial numbers.
 */
object CurrencyFormatter {

    private val indianLocale = Locale("en", "IN")
    private val symbols = DecimalFormatSymbols(indianLocale).apply {
        currencySymbol = "₹"
    }

    // Indian numbering format: e.g. ₹1,25,000.00
    private val rupeeFormatterWithDecimals = DecimalFormat("₹##,##,##0.00", symbols)
    private val rupeeFormatterNoDecimals = DecimalFormat("₹##,##,##0", symbols)
    private val plainDecimalFormatter = DecimalFormat("0.00", DecimalFormatSymbols(Locale.US))

    /**
     * Formats integer paise into a formatted Rupee string with the ₹ symbol.
     * Example: 15050L -> "₹150.50", 10000000L -> "₹1,00,000.00"
     */
    fun formatPaiseToRupees(paise: Long, showDecimalsAlways: Boolean = false): String {
        val rupees = paise / 100.0
        return if (!showDecimalsAlways && paise % 100L == 0L) {
            rupeeFormatterNoDecimals.format(rupees)
        } else {
            rupeeFormatterWithDecimals.format(rupees)
        }
    }

    /**
     * Formats integer paise into a clean decimal string for editing.
     * Example: 15050L -> "150.50", 50000L -> "500"
     */
    fun formatPaiseForInput(paise: Long): String {
        return if (paise % 100L == 0L) {
            (paise / 100L).toString()
        } else {
            val rupees = paise / 100.0
            plainDecimalFormatter.format(rupees)
        }
    }

    /**
     * Parses a user-entered Rupee string into 64-bit integer paise.
     * Accepts formats like "50", "150.50", " ₹ 1,200.75 ".
     * Returns null if string is empty, non-numeric, or <= 0.
     */
    fun parseRupeesToPaise(input: String): Long? {
        val cleaned = input.trim()
            .replace("₹", "")
            .replace(",", "")
            .trim()

        if (cleaned.isEmpty()) return null

        return try {
            val amountDouble = cleaned.toDouble()
            if (amountDouble <= 0.0 || amountDouble > 100_000_000.0) {
                null
            } else {
                // Round to nearest paise to prevent floating point imprecision
                Math.round(amountDouble * 100.0)
            }
        } catch (e: NumberFormatException) {
            null
        }
    }

    /**
     * Validates if the given input string represents a valid expense amount in Rupees.
     */
    fun isValidAmount(input: String): Boolean {
        return parseRupeesToPaise(input) != null
    }
}

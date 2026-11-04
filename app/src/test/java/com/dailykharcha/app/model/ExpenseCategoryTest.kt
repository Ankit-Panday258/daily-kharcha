package com.dailykharcha.app.model

import com.dailykharcha.app.data.model.ExpenseCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExpenseCategoryTest {

    @Test
    fun testAllRequiredCategoriesExist() {
        val names = ExpenseCategory.entries.map { it.name }
        assertTrue(names.contains("FOOD"))
        assertTrue(names.contains("PETROL"))
        assertTrue(names.contains("RECHARGE"))
        assertTrue(names.contains("RENT"))
        assertTrue(names.contains("OTHER"))
        assertEquals(5, ExpenseCategory.entries.size)
    }

    @Test
    fun testCategoryHindiTitles() {
        assertEquals("खाना", ExpenseCategory.FOOD.hindiTitle)
        assertEquals("पेट्रोल", ExpenseCategory.PETROL.hindiTitle)
        assertEquals("रिचार्ज", ExpenseCategory.RECHARGE.hindiTitle)
        assertEquals("किराया", ExpenseCategory.RENT.hindiTitle)
        assertEquals("अन्य", ExpenseCategory.OTHER.hindiTitle)
    }

    @Test
    fun testFromString() {
        assertEquals(ExpenseCategory.FOOD, ExpenseCategory.fromString("FOOD"))
        assertEquals(ExpenseCategory.PETROL, ExpenseCategory.fromString("petrol"))
        assertEquals(ExpenseCategory.RECHARGE, ExpenseCategory.fromString("Recharge"))
        assertEquals(ExpenseCategory.RENT, ExpenseCategory.fromString("RENT"))
        assertEquals(ExpenseCategory.OTHER, ExpenseCategory.fromString("OTHER"))
        // Fallback for unknown
        assertEquals(ExpenseCategory.OTHER, ExpenseCategory.fromString("UNKNOWN_XYZ"))
    }
}

package com.dailykharcha.app.data.local

import androidx.room.TypeConverter
import com.dailykharcha.app.data.model.ExpenseCategory

/**
 * Room TypeConverter for custom objects like ExpenseCategory.
 */
class Converters {

    @TypeConverter
    fun fromExpenseCategory(category: ExpenseCategory): String {
        return category.name
    }

    @TypeConverter
    fun toExpenseCategory(name: String): ExpenseCategory {
        return ExpenseCategory.fromString(name)
    }
}

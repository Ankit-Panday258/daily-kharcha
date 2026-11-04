package com.dailykharcha.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dailykharcha.app.data.model.ExpenseCategory

/**
 * Room entity representing an individual expense entry in Daily Kharcha.
 *
 * Monetary amounts are strictly stored as 64-bit integer paise (1 Rupee = 100 Paise)
 * to eliminate floating-point arithmetic errors.
 */
@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val amountPaise: Long,
    val category: ExpenseCategory,
    val dateEpochMillis: Long,
    val notes: String? = null,
    val createdAtMillis: Long = System.currentTimeMillis()
)

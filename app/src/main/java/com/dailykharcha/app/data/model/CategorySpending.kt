package com.dailykharcha.app.data.model

import androidx.compose.ui.graphics.Color

/**
 * Aggregated category spending data used for pie charts and breakdown lists.
 */
data class CategorySpending(
    val category: ExpenseCategory,
    val totalPaise: Long,
    val percentage: Float, // 0.0f to 100.0f
    val transactionCount: Int = 0,
    val displayColor: Color = category.color
)

package com.dailykharcha.app.data.repository

import com.dailykharcha.app.data.local.ExpenseEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface defining operations on expense data.
 */
interface ExpenseRepository {

    fun getAllExpenses(): Flow<List<ExpenseEntity>>

    fun getExpensesForDateRange(startMillis: Long, endMillis: Long): Flow<List<ExpenseEntity>>

    fun getExpensesForMonth(year: Int, month: Int): Flow<List<ExpenseEntity>>

    fun getRecentExpenses(limit: Int = 5): Flow<List<ExpenseEntity>>

    fun getExpenseById(id: Long): Flow<ExpenseEntity?>

    suspend fun getExpenseByIdDirect(id: Long): ExpenseEntity?

    suspend fun insertExpense(expense: ExpenseEntity): Long

    suspend fun updateExpense(expense: ExpenseEntity)

    suspend fun deleteExpense(expense: ExpenseEntity)

    suspend fun deleteExpenseById(id: Long)
}

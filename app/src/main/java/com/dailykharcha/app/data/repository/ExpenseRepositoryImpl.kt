package com.dailykharcha.app.data.repository

import com.dailykharcha.app.data.local.ExpenseDao
import com.dailykharcha.app.data.local.ExpenseEntity
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

/**
 * Concrete implementation of ExpenseRepository using Room ExpenseDao.
 */
class ExpenseRepositoryImpl(
    private val expenseDao: ExpenseDao
) : ExpenseRepository {

    override fun getAllExpenses(): Flow<List<ExpenseEntity>> {
        return expenseDao.getAllExpenses()
    }

    override fun getExpensesForDateRange(startMillis: Long, endMillis: Long): Flow<List<ExpenseEntity>> {
        return expenseDao.getExpensesByDateRange(startMillis, endMillis)
    }

    override fun getExpensesForMonth(year: Int, month: Int): Flow<List<ExpenseEntity>> {
        val calendar = Calendar.getInstance()
        // Month in Calendar is 0-indexed (0 = Jan, 11 = Dec)
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month - 1)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startMillis = calendar.timeInMillis

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endMillis = calendar.timeInMillis

        return expenseDao.getExpensesByDateRange(startMillis, endMillis)
    }

    override fun getRecentExpenses(limit: Int): Flow<List<ExpenseEntity>> {
        return expenseDao.getRecentExpenses(limit)
    }

    override fun getExpenseById(id: Long): Flow<ExpenseEntity?> {
        return expenseDao.getExpenseById(id)
    }

    override suspend fun getExpenseByIdDirect(id: Long): ExpenseEntity? {
        return expenseDao.getExpenseByIdDirect(id)
    }

    override suspend fun insertExpense(expense: ExpenseEntity): Long {
        return expenseDao.insertExpense(expense)
    }

    override suspend fun updateExpense(expense: ExpenseEntity) {
        expenseDao.updateExpense(expense)
    }

    override suspend fun deleteExpense(expense: ExpenseEntity) {
        expenseDao.deleteExpense(expense)
    }

    override suspend fun deleteExpenseById(id: Long) {
        expenseDao.deleteExpenseById(id)
    }
}

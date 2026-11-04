package com.dailykharcha.app.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for expenses table.
 */
@Dao
interface ExpenseDao {

    @Query("SELECT * FROM expenses ORDER BY dateEpochMillis DESC, id DESC")
    fun getAllExpenses(): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE dateEpochMillis >= :startMillis AND dateEpochMillis <= :endMillis ORDER BY dateEpochMillis DESC, id DESC")
    fun getExpensesByDateRange(startMillis: Long, endMillis: Long): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE id = :id LIMIT 1")
    fun getExpenseById(id: Long): Flow<ExpenseEntity?>

    @Query("SELECT * FROM expenses WHERE id = :id LIMIT 1")
    suspend fun getExpenseByIdDirect(id: Long): ExpenseEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity): Long

    @Update
    suspend fun updateExpense(expense: ExpenseEntity)

    @Delete
    suspend fun deleteExpense(expense: ExpenseEntity)

    @Query("DELETE FROM expenses WHERE id = :id")
    suspend fun deleteExpenseById(id: Long)

    @Query("SELECT SUM(amountPaise) FROM expenses WHERE dateEpochMillis >= :startMillis AND dateEpochMillis <= :endMillis")
    fun getTotalSpendingBetween(startMillis: Long, endMillis: Long): Flow<Long?>

    @Query("SELECT SUM(amountPaise) FROM expenses")
    fun getTotalSpendingAllTime(): Flow<Long?>

    @Query("SELECT * FROM expenses ORDER BY dateEpochMillis DESC, id DESC LIMIT :limit")
    fun getRecentExpenses(limit: Int): Flow<List<ExpenseEntity>>
}

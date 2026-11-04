package com.dailykharcha.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dailykharcha.app.data.local.ExpenseEntity
import com.dailykharcha.app.data.model.CategorySpending
import com.dailykharcha.app.data.model.ExpenseCategory
import com.dailykharcha.app.data.repository.ExpenseRepository
import com.dailykharcha.app.util.CurrencyFormatter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

/**
 * Main ViewModel for Daily Kharcha application.
 * Manages reactive expense data, monthly navigation, category aggregations, and CRUD operations.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ExpenseViewModel(
    private val repository: ExpenseRepository
) : ViewModel() {

    // Current selected Year and Month (1 = January, 12 = December)
    private val currentCalendar = Calendar.getInstance()
    private val _selectedYear = MutableStateFlow(currentCalendar.get(Calendar.YEAR))
    val selectedYear: StateFlow<Int> = _selectedYear.asStateFlow()

    private val _selectedMonth = MutableStateFlow(currentCalendar.get(Calendar.MONTH) + 1)
    val selectedMonth: StateFlow<Int> = _selectedMonth.asStateFlow()

    // Single-event UI messages (e.g. "Kharcha safaltapoorvak jod diya gaya!")
    private val _userMessage = MutableSharedFlow<String>()
    val userMessage: SharedFlow<String> = _userMessage.asSharedFlow()

    // All expenses sorted newest first
    val allExpenses: StateFlow<List<ExpenseEntity>> = repository.getAllExpenses()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Recent 5 expenses for the Dashboard
    val recentExpenses: StateFlow<List<ExpenseEntity>> = repository.getRecentExpenses(limit = 5)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Reactive expenses for currently selected month
    val monthlyExpenses: StateFlow<List<ExpenseEntity>> = combine(_selectedYear, _selectedMonth) { year, month ->
        Pair(year, month)
    }.flatMapLatest { (year, month) ->
        repository.getExpensesForMonth(year, month)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Current month total spending in Paise
    val monthlyTotalPaise: StateFlow<Long> = monthlyExpenses.map { expenses ->
        expenses.sumOf { it.amountPaise }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0L
    )

    // Category-wise spending breakdown for Pie Chart
    val categorySpendingList: StateFlow<List<CategorySpending>> = monthlyExpenses.combine(monthlyTotalPaise) { expenses, totalPaise ->
        if (totalPaise <= 0L || expenses.isEmpty()) {
            emptyList()
        } else {
            // Group expenses by category and calculate totals & percentages
            val grouped = expenses.groupBy { it.category }
            ExpenseCategory.entries.mapNotNull { category ->
                val categoryExpenses = grouped[category] ?: return@mapNotNull null
                val catPaise = categoryExpenses.sumOf { it.amountPaise }
                if (catPaise <= 0L) return@mapNotNull null
                val pct = (catPaise.toDouble() / totalPaise.toDouble()) * 100.0
                CategorySpending(
                    category = category,
                    totalPaise = catPaise,
                    percentage = pct.toFloat(),
                    transactionCount = categoryExpenses.size
                )
            }.sortedByDescending { it.totalPaise }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Month Navigation
    fun selectPreviousMonth() {
        if (_selectedMonth.value == 1) {
            _selectedYear.value -= 1
            _selectedMonth.value = 12
        } else {
            _selectedMonth.value -= 1
        }
    }

    fun selectNextMonth() {
        if (_selectedMonth.value == 12) {
            _selectedYear.value += 1
            _selectedMonth.value = 1
        } else {
            _selectedMonth.value += 1
        }
    }

    fun selectMonthYear(year: Int, month: Int) {
        _selectedYear.value = year
        _selectedMonth.value = month
    }

    fun resetToCurrentMonth() {
        val now = Calendar.getInstance()
        _selectedYear.value = now.get(Calendar.YEAR)
        _selectedMonth.value = now.get(Calendar.MONTH) + 1
    }

    // CRUD Operations
    suspend fun addExpense(
        title: String,
        amountInput: String,
        category: ExpenseCategory,
        dateEpochMillis: Long,
        notes: String?
    ): Result<Long> {
        val amountPaise = CurrencyFormatter.parseRupeesToPaise(amountInput)
            ?: return Result.failure(IllegalArgumentException("Kripya sahi rashi (amount) bharein"))

        val trimmedTitle = title.trim().ifEmpty { category.title }

        val newExpense = ExpenseEntity(
            title = trimmedTitle,
            amountPaise = amountPaise,
            category = category,
            dateEpochMillis = dateEpochMillis,
            notes = notes?.trim()?.ifEmpty { null }
        )

        return try {
            val id = repository.insertExpense(newExpense)
            _userMessage.emit("Kharcha safaltapoorvak jod diya gaya!")
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateExpense(
        id: Long,
        title: String,
        amountInput: String,
        category: ExpenseCategory,
        dateEpochMillis: Long,
        notes: String?
    ): Result<Unit> {
        val amountPaise = CurrencyFormatter.parseRupeesToPaise(amountInput)
            ?: return Result.failure(IllegalArgumentException("Kripya sahi rashi (amount) bharein"))

        val trimmedTitle = title.trim().ifEmpty { category.title }

        val updated = ExpenseEntity(
            id = id,
            title = trimmedTitle,
            amountPaise = amountPaise,
            category = category,
            dateEpochMillis = dateEpochMillis,
            notes = notes?.trim()?.ifEmpty { null }
        )

        return try {
            repository.updateExpense(updated)
            _userMessage.emit("Kharcha badal diya gaya!")
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun deleteExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
            _userMessage.emit("Kharcha hata diya gaya")
        }
    }

    suspend fun getExpenseByIdDirect(id: Long): ExpenseEntity? {
        return repository.getExpenseByIdDirect(id)
    }
}

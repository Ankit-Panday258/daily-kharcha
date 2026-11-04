package com.dailykharcha.app.ui.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dailykharcha.app.data.local.ExpenseEntity
import com.dailykharcha.app.data.model.ExpenseCategory
import com.dailykharcha.app.ui.components.ConfirmDeleteDialog
import com.dailykharcha.app.ui.components.ExpenseCard
import com.dailykharcha.app.ui.theme.BackgroundWhite
import com.dailykharcha.app.ui.theme.PrimaryGreen
import com.dailykharcha.app.ui.theme.TextPrimary
import com.dailykharcha.app.ui.theme.TextSecondary
import com.dailykharcha.app.ui.viewmodel.ExpenseViewModel
import com.dailykharcha.app.util.CurrencyFormatter
import com.dailykharcha.app.util.DateFormatter
import java.util.Calendar

/**
 * Screen 3: Expense History ("Kharcha Itihaas").
 *
 * Features:
 * - Shows all saved expenses (newest first)
 * - Category, amount, and date
 * - Filter by month (Current month vs All Months)
 * - Filter by Category
 * - Search by title/note
 * - Edit existing expense
 * - Delete expense with confirmation dialog
 * - Summary strip of matching transactions & total ₹
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: ExpenseViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToAddExpense: () -> Unit,
    onNavigateToEditExpense: (Long) -> Unit
) {
    val allExpenses by viewModel.allExpenses.collectAsState()
    val selectedYear by viewModel.selectedYear.collectAsState()
    val selectedMonth by viewModel.selectedMonth.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var filterCategory by remember { mutableStateOf<ExpenseCategory?>(null) }
    var onlyCurrentMonth by remember { mutableStateOf(false) }
    var expenseToDelete by remember { mutableStateOf<ExpenseEntity?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.userMessage.collect { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    if (expenseToDelete != null) {
        ConfirmDeleteDialog(
            expense = expenseToDelete!!,
            onConfirm = {
                viewModel.deleteExpense(expenseToDelete!!)
                expenseToDelete = null
            },
            onDismiss = {
                expenseToDelete = null
            }
        )
    }

    // Filter Logic
    val filteredExpenses = remember(allExpenses, searchQuery, filterCategory, onlyCurrentMonth, selectedYear, selectedMonth) {
        val calendar = Calendar.getInstance()
        allExpenses.filter { expense ->
            val matchesCategory = filterCategory == null || expense.category == filterCategory
            val matchesSearch = searchQuery.isBlank() ||
                    expense.title.contains(searchQuery, ignoreCase = true) ||
                    (expense.notes?.contains(searchQuery, ignoreCase = true) == true) ||
                    expense.category.title.contains(searchQuery, ignoreCase = true)

            val matchesMonth = if (!onlyCurrentMonth) {
                true
            } else {
                calendar.timeInMillis = expense.dateEpochMillis
                val expYear = calendar.get(Calendar.YEAR)
                val expMonth = calendar.get(Calendar.MONTH) + 1
                expYear == selectedYear && expMonth == selectedMonth
            }

            matchesCategory && matchesSearch && matchesMonth
        }
    }

    val totalFilteredPaise = remember(filteredExpenses) {
        filteredExpenses.sumOf { it.amountPaise }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Kharcha Itihaas",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Sabhi Kharche (${filteredExpenses.size})",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Peeche Jayein",
                            tint = TextPrimary
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onNavigateToAddExpense,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Naya Kharcha",
                            tint = PrimaryGreen
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundWhite
                )
            )
        },
        containerColor = BackgroundWhite
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search Input Field
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Kharcha khojein (Search)...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = TextSecondary
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Saaf Karein",
                                    tint = TextSecondary
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryGreen,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
            }

            // Month & Category Filter Chips Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Month Toggle Chip
                val monthLabel = if (onlyCurrentMonth) DateFormatter.formatMonthYear(selectedYear, selectedMonth) else "Sabhi Mahine (All)"
                FilterToggleChip(
                    text = monthLabel,
                    isSelected = onlyCurrentMonth,
                    icon = Icons.Default.FilterList,
                    onClick = { onlyCurrentMonth = !onlyCurrentMonth }
                )

                // Category Chips
                ExpenseCategory.entries.forEach { category ->
                    val isSelected = filterCategory == category
                    FilterToggleChip(
                        text = category.title,
                        isSelected = isSelected,
                        tintColor = category.color,
                        onClick = {
                            filterCategory = if (isSelected) null else category
                        }
                    )
                }
            }

            // Total Amount Banner for active filter
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Kul Rashi (Total)",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                        Text(
                            text = CurrencyFormatter.formatPaiseToRupees(totalFilteredPaise),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryGreen
                        )
                    }

                    Text(
                        text = "${filteredExpenses.size} Records",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary
                    )
                }
            }

            // List of Expenses
            if (filteredExpenses.isEmpty()) {
                EmptyHistoryState(
                    hasFilters = searchQuery.isNotBlank() || filterCategory != null || onlyCurrentMonth,
                    onClearFilters = {
                        searchQuery = ""
                        filterCategory = null
                        onlyCurrentMonth = false
                    },
                    onAddExpense = onNavigateToAddExpense
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredExpenses, key = { it.id }) { expense ->
                        ExpenseCard(
                            expense = expense,
                            onEditClick = { onNavigateToEditExpense(expense.id) },
                            onDeleteClick = { expenseToDelete = expense }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(30.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterToggleChip(
    text: String,
    isSelected: Boolean,
    tintColor: Color = PrimaryGreen,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    onClick: () -> Unit
) {
    val bgColor = if (isSelected) tintColor.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
    val borderColor = if (isSelected) tintColor else MaterialTheme.colorScheme.outline
    val textColor = if (isSelected) tintColor else TextSecondary

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = textColor
        )
    }
}

@Composable
private fun EmptyHistoryState(
    hasFilters: Boolean,
    onClearFilters: () -> Unit,
    onAddExpense: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(Color(0xFFF1F5F9), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Receipt,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (hasFilters) "Koi Kharcha Nahi Mila" else "Itihaas Khali Hai",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = if (hasFilters)
                    "Aapke filter ke anusaar koi kharcha record nahi hai."
                else
                    "Abhi tak koi kharcha darj nahi kiya gaya hai. Pehla kharcha jodein!",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (hasFilters) {
                Button(
                    onClick = onClearFilters,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text(text = "Filters Hatayein", fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = onAddExpense,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                ) {
                    Text(text = "+ Pehla Kharcha Jodein", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

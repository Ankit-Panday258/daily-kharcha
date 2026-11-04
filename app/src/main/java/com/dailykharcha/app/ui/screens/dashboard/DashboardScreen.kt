package com.dailykharcha.app.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dailykharcha.app.data.local.ExpenseEntity
import com.dailykharcha.app.ui.components.CategoryPieChart
import com.dailykharcha.app.ui.components.ConfirmDeleteDialog
import com.dailykharcha.app.ui.components.ExpenseCard
import com.dailykharcha.app.ui.components.SummaryCard
import com.dailykharcha.app.ui.theme.BackgroundWhite
import com.dailykharcha.app.ui.theme.PrimaryGreen
import com.dailykharcha.app.ui.theme.TextPrimary
import com.dailykharcha.app.ui.theme.TextSecondary
import com.dailykharcha.app.ui.viewmodel.ExpenseViewModel

/**
 * Screen 1: Home Dashboard for Daily Kharcha.
 *
 * Contains:
 * - App title: Daily Kharcha
 * - Subtitle: Apna Kharcha Track Karein
 * - Current month's total spending & summary card
 * - Interactive category-wise pie chart
 * - Recent expense transactions
 * - Large Add Expense button
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: ExpenseViewModel,
    onNavigateToAddExpense: () -> Unit,
    onNavigateToEditExpense: (Long) -> Unit,
    onNavigateToHistory: () -> Unit
) {
    val selectedYear by viewModel.selectedYear.collectAsState()
    val selectedMonth by viewModel.selectedMonth.collectAsState()
    val monthlyTotalPaise by viewModel.monthlyTotalPaise.collectAsState()
    val monthlyExpenses by viewModel.monthlyExpenses.collectAsState()
    val categorySpendingList by viewModel.categorySpendingList.collectAsState()
    val recentExpenses by viewModel.recentExpenses.collectAsState()

    var expenseToDelete by remember { mutableStateOf<ExpenseEntity?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.userMessage.collect { message ->
            snackbarHostState.showSnackbar(message)
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

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Daily Kharcha",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = PrimaryGreen,
                                fontSize = 22.sp
                            )
                        )
                        Text(
                            text = "Apna Kharcha Track Karein",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onNavigateToHistory,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "Kharcha Itihaas",
                            tint = PrimaryGreen
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundWhite
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToAddExpense,
                containerColor = PrimaryGreen,
                contentColor = Color.White,
                shape = RoundedCornerShape(20.dp),
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Kharcha Jodein",
                        modifier = Modifier.size(24.dp)
                    )
                },
                text = {
                    Text(
                        text = "+ Kharcha Jodein",
                        style = MaterialTheme.typography.labelLarge.copy(fontSize = 16.sp)
                    )
                }
            )
        },
        containerColor = BackgroundWhite
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Summary Card with Month Controls
            item {
                SummaryCard(
                    totalPaise = monthlyTotalPaise,
                    transactionCount = monthlyExpenses.size,
                    selectedYear = selectedYear,
                    selectedMonth = selectedMonth,
                    onPreviousMonth = { viewModel.selectPreviousMonth() },
                    onNextMonth = { viewModel.selectNextMonth() }
                )
            }

            // Interactive Category-wise Pie Chart
            item {
                CategoryPieChart(
                    categorySpendingList = categorySpendingList,
                    totalPaise = monthlyTotalPaise
                )
            }

            // Quick Add Expense Banner Button (Large Touch-Friendly)
            item {
                Button(
                    onClick = onNavigateToAddExpense,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryGreen
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Naya Kharcha Jodein (Add Expense)",
                        style = MaterialTheme.typography.labelLarge.copy(fontSize = 16.sp),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Recent Transactions Section Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ReceiptLong,
                            contentDescription = null,
                            tint = PrimaryGreen,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Haal Hi Ke Kharche",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    if (recentExpenses.isNotEmpty()) {
                        TextButton(onClick = onNavigateToHistory) {
                            Text(
                                text = "Sabhi Dekhein >",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
            }

            // Recent Transactions List Items
            if (recentExpenses.isEmpty()) {
                item {
                    EmptyRecentTransactionsCard(onAddClick = onNavigateToAddExpense)
                }
            } else {
                items(recentExpenses, key = { it.id }) { expense ->
                    ExpenseCard(
                        expense = expense,
                        onEditClick = { onNavigateToEditExpense(expense.id) },
                        onDeleteClick = { expenseToDelete = expense }
                    )
                }
            }

            // Extra space at bottom for FAB clearance
            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }
    }
}

@Composable
private fun EmptyRecentTransactionsCard(onAddClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Abhi Koi Kharcha Darj Nahi Hai",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Apna daily kharcha jodane ke liye 'Kharcha Jodein' button par click karein.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(14.dp))
            Button(
                onClick = onAddClick,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
            ) {
                Text(text = "+ Kharcha Jodein", fontWeight = FontWeight.Bold)
            }
        }
    }
}

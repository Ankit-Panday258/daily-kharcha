package com.dailykharcha.app.ui.screens.add_edit

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Label
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dailykharcha.app.data.model.ExpenseCategory
import com.dailykharcha.app.ui.components.CategorySelectionCard
import com.dailykharcha.app.ui.theme.BackgroundWhite
import com.dailykharcha.app.ui.theme.PrimaryGreen
import com.dailykharcha.app.ui.theme.TextPrimary
import com.dailykharcha.app.ui.theme.TextSecondary
import com.dailykharcha.app.ui.viewmodel.ExpenseViewModel
import com.dailykharcha.app.util.CurrencyFormatter
import com.dailykharcha.app.util.DateFormatter
import kotlinx.coroutines.launch
import java.util.Calendar

/**
 * Screen 2: Add or Edit Expense Form.
 *
 * Contains:
 * - Amount in ₹ (large readable input with validation)
 * - Category selection:
 *   1. Khana (Food)
 *   2. Petrol
 *   3. Recharge
 *   4. Kiraya (Rent)
 *   5. Other
 * - Expense Title/Purpose
 * - Date picker (automatically selects today's date)
 * - Large touch-friendly Save Expense button
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditExpenseScreen(
    expenseId: Long?,
    viewModel: ExpenseViewModel,
    onNavigateBack: () -> Unit
) {
    val isEditing = expenseId != null && expenseId > 0

    var amountText by remember { mutableStateOf("") }
    var titleText by remember { mutableStateOf("") }
    var notesText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(ExpenseCategory.FOOD) }
    var selectedDateMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }

    var amountError by remember { mutableStateOf<String?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current
    val amountFocusRequester = remember { FocusRequester() }

    // Load existing expense if editing
    LaunchedEffect(expenseId) {
        if (isEditing) {
            val existing = viewModel.getExpenseByIdDirect(expenseId)
            if (existing != null) {
                amountText = CurrencyFormatter.formatPaiseForInput(existing.amountPaise)
                titleText = existing.title
                notesText = existing.notes ?: ""
                selectedCategory = existing.category
                selectedDateMillis = existing.dateEpochMillis
            }
        }
    }

    // Material 3 Date Picker Dialog
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDateMillis
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            selectedDateMillis = it
                        }
                        showDatePicker = false
                    }
                ) {
                    Text(text = "Theek Hai (OK)", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(text = "Radd Karein (Cancel)")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditing) "Kharcha Badlein" else "Naya Kharcha Jodein",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // 1. Amount Card with Big Display
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Text(
                        text = "Kharcha Rashi (Amount)",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = amountText,
                        onValueChange = { input ->
                            // Allow only valid numbers and at most one decimal point
                            if (input.isEmpty() || input.matches(Regex("""^\d*\.?\d{0,2}$"""))) {
                                amountText = input
                                amountError = null
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(amountFocusRequester),
                        placeholder = {
                            Text(
                                text = "0",
                                style = MaterialTheme.typography.displayMedium.copy(
                                    fontSize = 32.sp,
                                    color = TextSecondary.copy(alpha = 0.4f)
                                )
                            )
                        },
                        leadingIcon = {
                            Text(
                                text = "₹",
                                style = MaterialTheme.typography.displayMedium.copy(
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryGreen
                                ),
                                modifier = Modifier.padding(start = 12.dp, end = 4.dp)
                            )
                        },
                        textStyle = MaterialTheme.typography.displayMedium.copy(
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        ),
                        singleLine = true,
                        isError = amountError != null,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Next
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryGreen,
                            unfocusedBorderColor = Color.Transparent,
                            errorBorderColor = MaterialTheme.colorScheme.error,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        )
                    )

                    if (amountError != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = amountError!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // 2. Category Selection Header & Options
            Column {
                Text(
                    text = "Category Chunein",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Ye kharcha kahan hua?",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(10.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ExpenseCategory.entries.forEach { category ->
                        CategorySelectionCard(
                            category = category,
                            isSelected = selectedCategory == category,
                            onSelect = {
                                selectedCategory = category
                                focusManager.clearFocus()
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // 3. Date Selection Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "Tareekh",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Tareekh (Date)",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextSecondary
                        )
                        Text(
                            text = DateFormatter.formatFriendlyDate(selectedDateMillis),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    Text(
                        text = "Badlein",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            // 4. Expense Title / Purpose (Optional)
            OutlinedTextField(
                value = titleText,
                onValueChange = { titleText = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Kiska kharcha? (Title)") },
                placeholder = { Text("Jaise: Doodh, Chai-Nashta, Jio recharge...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Label,
                        contentDescription = null,
                        tint = TextSecondary
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryGreen,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            // 5. Notes / Details (Optional)
            OutlinedTextField(
                value = notesText,
                onValueChange = { notesText = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Extra Note / Vivaran (Optional)") },
                placeholder = { Text("Koi aur jankari...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = null,
                        tint = TextSecondary
                    )
                },
                maxLines = 3,
                shape = RoundedCornerShape(14.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryGreen,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            // 6. Large Save Expense Button
            Button(
                onClick = {
                    focusManager.clearFocus()

                    // Validation
                    if (!CurrencyFormatter.isValidAmount(amountText)) {
                        amountError = "Kripya sahi rashi bharein (Jaise: 150 ya 150.50)"
                        return@Button
                    }

                    isSaving = true
                    coroutineScope.launch {
                        val result = if (isEditing && expenseId != null) {
                            viewModel.updateExpense(
                                id = expenseId,
                                title = titleText,
                                amountInput = amountText,
                                category = selectedCategory,
                                dateEpochMillis = selectedDateMillis,
                                notes = notesText
                            )
                        } else {
                            viewModel.addExpense(
                                title = titleText,
                                amountInput = amountText,
                                category = selectedCategory,
                                dateEpochMillis = selectedDateMillis,
                                notes = notesText
                            )
                        }

                        isSaving = false
                        if (result.isSuccess) {
                            onNavigateBack()
                        } else {
                            snackbarHostState.showSnackbar(
                                result.exceptionOrNull()?.message ?: "Kharcha save nahi ho saka"
                            )
                        }
                    }
                },
                enabled = !isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryGreen,
                    disabledContainerColor = PrimaryGreen.copy(alpha = 0.5f)
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isEditing) "Kharcha Update Karein" else "Kharcha Save Karein",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

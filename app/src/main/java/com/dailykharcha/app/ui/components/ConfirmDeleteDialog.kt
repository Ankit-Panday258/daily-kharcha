package com.dailykharcha.app.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dailykharcha.app.data.local.ExpenseEntity
import com.dailykharcha.app.util.CurrencyFormatter

/**
 * Confirmation dialog before deleting an expense.
 */
@Composable
fun ConfirmDeleteDialog(
    expense: ExpenseEntity,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(
                text = "Kharcha Delete Karein?",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = "Kya aap sach me '${expense.title}' (${CurrencyFormatter.formatPaiseToRupees(expense.amountPaise)}) ko delete karna chahte hain? Ye wapas nahi aayega.",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text(
                    text = "Hatao (Delete)",
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Rakhne Do (Cancel)",
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    )
}

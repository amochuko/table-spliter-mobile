package com.ochuko.tabsplit.ui.components.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import android.util.Log
import com.ochuko.tabsplit.ui.expense.ExpenseViewModel

@Composable
fun AddExpenseDialog(
    sessionId: String,
    onClose: () -> Unit,
    expensesViewModel: ExpenseViewModel
) {
    val context = LocalContext.current
    var memo by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var submit by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = { onClose() },
        title = { Text(text = "Add Expense") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = memo,
                    onValueChange = {
                        submit = false
                        memo = it
                    },
                    label = {
                        Text(
                            if (submit && memo.isBlank()) "A memo is required!"
                            else "Memo"
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = {
                        Text(
                            if (submit && amount.isBlank()) "An amount is required!"
                            else "Amount"
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                submit = true
                if (memo.isBlank() || amount.isBlank()) {
                    Toast.makeText(context, "Memo and amount required!", Toast.LENGTH_SHORT).show()
                } else {
                    scope.launch {
                        try {
                            // Call your store
                            expensesViewModel.addExpense(
                                sessionId,
                                memo,
                                amount.toDouble(),
                            )

                            onClose()

                        } catch (e: Exception) {
                            Log.e("AddExpense", "Failed to add expense", e)

                            Toast.makeText(context, "Failed to add expense", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }

                }
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onClose) {
                Text("Cancel")
            }
        }
    )
}

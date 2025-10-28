package com.partum.tabsplit.ui.components.ui

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
import androidx.compose.ui.res.stringResource
import com.partum.tabsplit.R
import com.partum.tabsplit.ui.auth.AuthViewModel
import com.partum.tabsplit.ui.expense.ExpenseViewModel

@Composable
fun AddExpenseDialog(
    sessionId: String,
    onClose: () -> Unit,
    expensesViewModel: ExpenseViewModel,
    hasSetZaddr: Boolean
) {
    val context = LocalContext.current
    var memo by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var submit by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = { onClose() },
        title = { Text(text = stringResource(R.string.add_expense)) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    enabled = !hasSetZaddr,
                    value = memo,
                    onValueChange = {
                        submit = false
                        memo = it
                    },
                    label = {
                        Text(
                            if (submit && memo.isBlank()) stringResource(R.string.a_memo_is_required)
                            else stringResource(R.string.memo)
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    enabled = !hasSetZaddr,
                    value = amount,
                    onValueChange = { amount = it },
                    label = {
                        Text(
                            if (submit && amount.isBlank()) stringResource(R.string.an_amount_is_required)
                            else stringResource(R.string.amount)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                if (hasSetZaddr) {
                    Text(
                        text = stringResource(R.string.you_need_to_set_up_your_zcash_address_first_sapling_or_orchard),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.error.copy(alpha = 0.5f),
                        modifier = Modifier.padding(top = 24.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = !hasSetZaddr,
                onClick = {
                submit = true
                if (memo.isBlank() || amount.isBlank()) {
                    Toast.makeText(context,
                        context.getString(R.string.memo_and_amount_required), Toast.LENGTH_SHORT).show()
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

                            Toast.makeText(context,
                                context.getString(R.string.failed_to_add_expense), Toast.LENGTH_SHORT)
                                .show()
                        }
                    }

                }
            },
                ) {
                Text(text = stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onClose) {
                Text(text = stringResource(R.string.cancel))
            }
        }
    )
}

package com.ochuko.tabsplit.ui.components

import androidx.compose.material3.AlertDialog
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.ochuko.tabsplit.models.User
import com.ochuko.tabsplit.store.AuthStore
import kotlinx.coroutines.launch
import  android.util.Log
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator

@Composable
fun WalletDialog(
    authStore: AuthStore,
    user: User?,
    onDismiss: () -> Unit,
    onSave: suspend (zaddr: String) -> Boolean = { _ -> true }
) {

    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    var zaddr by remember { mutableStateOf(user?.zaddr ?: "") }
    var saving by remember { mutableStateOf(false) }

    AlertDialog(
        title = { Text("Wallet Address") },
        text = {
            Column {
                Text(
                    if (user?.zaddr == null) "Enter your wallet address (ZADDR)"
                    else "Edit your wallet address"
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = zaddr,
                    onValueChange = { zaddr = it },
                    label = { Text("ZADDR") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(
                onClick = {
                    if (zaddr.isBlank()) {
                        Toast.makeText(ctx, "Wallet address required", Toast.LENGTH_SHORT).show()

                        return@TextButton
                    }

                    // Launch backend api in the background
                    scope.launch {
                        saving = true
                        val ok = try {
                            onSave(zaddr)
                        } catch (e: Exception) {
                            Log.e("WalletDialog", "save failed", e)
                            false
                        }

                        saving = false
                        if (ok) {
                            // update local store
                            // save zaddr
                            val currentToken = authStore.getToken()
                            val updatedUser = user?.copy(zaddr = zaddr)
                            if (updatedUser != null) {

                                authStore.setUser(updatedUser, currentToken)
                            } else {

                            }

                            Toast.makeText(ctx, "Wallet updated", Toast.LENGTH_SHORT).show()
                            onDismiss()
                        } else {

                            Toast.makeText(ctx, "Wallet save failed. Try again", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }

                },
                enabled = !saving
            ) {
                if (saving) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                } else {
                    Text("Save")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = { if (!saving) onDismiss() }) {
                Text("Cancel")
            }
        },
    )
}
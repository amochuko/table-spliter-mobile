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
import com.ochuko.tabsplit.data.model.User
import com.ochuko.tabsplit.ui.auth.AuthViewModel
import kotlinx.coroutines.launch
import  android.util.Log
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp

@Composable
fun WalletDialog(
    authViewModel: AuthViewModel,
    user: User?,
    onDismiss: () -> Unit,
    onSave: suspend (zaddr: String) -> Boolean = { _ -> true }
) {

    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    var zaddr by remember { mutableStateOf(user?.zaddr ?: "") }
    var saving by remember { mutableStateOf(false) }
    var validationError by remember { mutableStateOf<String?>(null) }

    fun isValidaZAddress(addr: String): Boolean {
        val normalized = addr.trim()
        val isSapling = normalized.startsWith("zs1") && normalized.length in 76..78
        val isOrchard = normalized.startsWith("u1") && normalized.length in 78..88

        return isSapling || isOrchard
    }

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
                    onValueChange = {
                        zaddr = it.trim()
                        validationError = when {
                            zaddr.isBlank() -> null
                            isValidaZAddress(zaddr) -> null
                            else -> "Invalid Zcash address. Must be Sapling (za...) " +
                                    "or Orchard (u1...)"
                        }
                    },
                    label = { Text("ZADDR") },
                    modifier = Modifier.fillMaxWidth()
                )

                if (validationError != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = validationError ?: "",
                        color = Color.Red,
                        fontSize = 12.sp
                    )
                }
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
                            val currentToken = authViewModel.getToken()
                            val updatedUser = user?.copy(zaddr = zaddr)

                            if (updatedUser != null) {
//                                authViewModel.setUser(updatedUser, currentToken)
                                authViewModel.saveToken(currentToken)
                            }

                            Toast.makeText(ctx, "Wallet updated", Toast.LENGTH_SHORT).show()
                            onDismiss()
                        } else {

                            Toast.makeText(
                                ctx,
                                "Wallet save failed. Try again",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                },
                enabled = !saving && validationError == null && zaddr.isNotBlank()
            ) {
                if (saving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp
                    )
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
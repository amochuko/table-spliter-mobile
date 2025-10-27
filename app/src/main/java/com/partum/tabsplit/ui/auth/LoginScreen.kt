package com.partum.tabsplit.ui.auth

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.partum.tabsplit.R
import com.partum.tabsplit.ui.session.SessionViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onSignupClick: () -> Unit,
    authViewModel: AuthViewModel,
    sessionViewModel: SessionViewModel
) {
    val authUiState by authViewModel.uiState.collectAsState()
    val sessionUiState by sessionViewModel.uiState.collectAsState()
    val pendingInviteCode = sessionUiState.pendingInviteCode

    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }


    // Whenever the uiState changes, check if the user is logged in.
    LaunchedEffect(authUiState.isLoggedIn) {
        if (authUiState.isLoggedIn) {
            onLoginSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp), contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                stringResource(R.string.login_label_txt),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )

            if (authUiState.error != null) {
                Text(
                    authUiState.error ?: "",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    if (email.isBlank() || password.isBlank()) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.email_and_password_required),
                            Toast.LENGTH_SHORT
                        ).show()

                        return@Button
                    }

                    try {
                        // Try login
                        authViewModel.login(email, password)

                        if (authUiState.loading) return@Button

                        // Started with a pending invite code
                        pendingInviteCode?.let { code ->
                            sessionViewModel.joinSessionByInvite(code)

                            sessionViewModel.setPendingInviteCode(null)
                        }

                        onLoginSuccess()

                    } catch (e: CancellationException) {
                        Log.e(
                            "LoginScreen",
                            "Coroutine cancelled due to recomposition or navigation"
                        )
                    } catch (e: Exception) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.login_failed_txt),
                            Toast.LENGTH_SHORT
                        ).show()

                        Log.e("LoginError", e.message.toString())
                    }
                }, modifier = Modifier.fillMaxWidth(), enabled = !authUiState.loading
            ) {
                if (authUiState.loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Login")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.padding(top = 8.dp)) {
                Text(
                    stringResource(R.string.don_t_have_an_account_txt),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.padding(top = 8.dp)
                )

                Text(
                    stringResource(R.string.sign_up_txt),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .clickable { onSignupClick() })
            }
        }
    }
}

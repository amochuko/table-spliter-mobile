package com.ochuko.tabsplit.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ochuko.tabsplit.store.AppStore
import com.ochuko.tabsplit.store.AuthViewModel
import kotlinx.coroutines.launch
import android.util.Log

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onSignupClick: () -> Unit,
    authViewModel: AuthViewModel = viewModel(),
    appStore: AppStore = viewModel()
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    val token by authViewModel.token.collectAsState()
    val pendingInviteCode by appStore.pendingInviteCode.collectAsState()

    // Redirect if already logged in
    LaunchedEffect(token) {
        if (!token.isNullOrEmpty()) {
            onLoginSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "Login",
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

            if (error.isNotEmpty()) {
                Text(
                    error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    scope.launch {
                        try {

                            // Try login
                            val res = authViewModel.login(email, password)?.let { (user, token) ->
                                appStore.setUser(user, token)

                            } ?: run {
                                error = "Invalid credentials"

                                return@launch
                            }

                            // Handle pending invite
                            pendingInviteCode?.let { code ->
                                val joinedSession = appStore.joinSessionByInvite(code)

                                if (joinedSession != null) {
                                    appStore.addSession(joinedSession)
                                    appStore.setPendingInviteCode(null)

                                    // On success, trigger navigation
                                    onLoginSuccess()
                                    return@launch
                                }
                            }

                            appStore.loadSessions()
                            onLoginSuccess()

                        } catch (e: Exception) {
                            Toast.makeText(context, "Login failed", Toast.LENGTH_SHORT).show()
                            error = "Unknown error"
                            Log.e("LoginError", e.message.toString())
                            e.printStackTrace()

                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Login")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "Don’t have an account? Sign up",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .padding(top = 8.dp)
                    .clickable { onSignupClick() }
            )
        }
    }
}

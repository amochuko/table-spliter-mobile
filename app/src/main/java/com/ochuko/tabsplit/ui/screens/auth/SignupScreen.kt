package com.ochuko.tabsplit.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
//import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ochuko.tabsplit.store.AppStore
import kotlinx.coroutines.launch
import com.ochuko.tabsplit.store.AuthViewModel


@Composable
fun SignupScreen(
    onSignupSuccess: () -> Unit,
    onLoginClick: () -> Unit,
    authViewModel: AuthViewModel = viewModel(),
    appStore: AppStore = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    val token by authViewModel.token.collectAsState()
    val pendingInviteCode by appStore.pendingInviteCode.collectAsState()

    // Redirect if already signed up/logged in
    LaunchedEffect(token) {
        if (!token.isNullOrEmpty()) {
            onSignupSuccess()
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
                "Sign up",
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
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
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

                            // Try signup
                            val res = authViewModel.register(email, password)?.let { (user,
                                                                                         token) ->
                                appStore.setUser(user, token)
                            } ?: run {
                                error = "Signup failed!"

                                return@launch
                            }


                            // Handle invite if exist
                            pendingInviteCode?.let { code ->
                                val joinedSession = appStore.joinSessionByInvite(code)

                                if (joinedSession != null) {

                                    appStore.addSession(joinedSession)
                                    appStore.setPendingInviteCode(null)
                                }
                            }

                            onSignupSuccess()
                        } catch (e: Exception) {
                            error = "Invalid credentials!"
                            e.printStackTrace()
                            Toast.makeText(context, "Signup failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Sign up")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(

                "Already got an account? login",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme
                        .colorScheme.primary
                ),
                modifier = Modifier
                    .padding(top = 8.dp)
                    .clickable {
                        onLoginClick()
                    }
            )
        }
    }
}

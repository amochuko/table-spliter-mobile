package com.partum.tabsplit.ui.auth

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import com.partum.tabsplit.ui.navigation.Screen
import com.partum.tabsplit.ui.session.SessionViewModel


@Composable
fun SignupScreen(
    navController: NavHostController,
    onSignupSuccess: () -> Unit,
    onLoginClick: () -> Unit,
    authViewModel: AuthViewModel,
    sessionViewModel: SessionViewModel
) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    val authUiState by authViewModel.uiState.collectAsState()
    val sessionUiState by sessionViewModel.uiState.collectAsState()
    val pendingInviteCode = sessionUiState.pendingInviteCode

    // Redirect if already signed up/logged in
    LaunchedEffect(authUiState.token) {
        if (!authUiState.token.isNullOrEmpty()) {
            onSignupSuccess()
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
                            authViewModel.signup(email, password)

                            // Handle invite if exist
                            pendingInviteCode?.let { code ->
                                val joinedSession = sessionViewModel.joinSessionByInvite(code)
                                sessionViewModel.setPendingInviteCode(null)
                            }

                            // Navigate **directly** after successful signup
                            navController.navigate(Screen.Sessions.route) {
                                popUpTo(Screen.Signup.route) { inclusive = true }
                                launchSingleTop = true
                            }

                        } catch (e: Exception) {
                            error = "Invalid credentials!"
                            Log.e("SignupError", e.message.toString())
                            e.printStackTrace()
                            Toast.makeText(context, "Signup failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                }, modifier = Modifier.fillMaxWidth()
            ) {
                Text("Sign up")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(

                "Already got an account? Login", style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.primary
                ), modifier = Modifier
                    .padding(top = 8.dp)
                    .clickable {
                        onLoginClick()
                    })
        }
    }
}

package com.partum.tabsplit

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import com.partum.tabsplit.ui.theme.TabSplitTheme
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.partum.tabsplit.ui.auth.AuthViewModel
import com.partum.tabsplit.ui.navigation.AppNavHost
import com.partum.tabsplit.ui.navigation.Screen
import android.util.Log
import com.partum.tabsplit.di.LocalViewModelFactory
import com.partum.tabsplit.di.injectedViewModel
import com.partum.tabsplit.ui.expense.ExpenseViewModel
import com.partum.tabsplit.ui.participant.ParticipantViewModel
import com.partum.tabsplit.ui.session.SessionViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainActivity : ComponentActivity() {

    // Activity-level flow to surface deep-link updates into Compose (handles onNewIntent)
    private val deepLinkFlow = MutableStateFlow<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // handle deep link
        deepLinkFlow.value = intent?.data?.lastPathSegment

        setContent {
            val appContainer = (application as TabSplit).appContainer
            val navController = rememberNavController()

            CompositionLocalProvider(
                LocalViewModelFactory provides appContainer.viewModelFactory
            ) {

                val authViewModel: AuthViewModel = injectedViewModel()
                val sessionViewModel: SessionViewModel = injectedViewModel()
                val expenseViewModel: ExpenseViewModel = injectedViewModel()
                val participantViewModel: ParticipantViewModel = injectedViewModel()

                val authUiState by authViewModel.uiState.collectAsState()

                // observe deep link
                val joinCode by deepLinkFlow.asStateFlow().collectAsState()

                TabSplitTheme {
                    // Wait for authStore to finish loading
                    if (authUiState.loading) {
                        Box(
                            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else {
                        // Mount the NavHost (keeps your three-arg API)
                        AppNavHost(
                            navController,
                            authViewModel,
                            sessionViewModel,
                            expenseViewModel,
                            participantViewModel
                        )

                        // Wait until appStore finishes loading
                        LaunchedEffect(authUiState.token) {
                            if (authUiState.token.isNullOrEmpty()) {
                                try {
                                    navController.navigate(Screen.Login.route) {
                                        popUpTo(0)
                                    }
                                } catch (e: Exception) {
                                    Log.e("MainActivity", "navigate to login failed")
                                }
                            } else {
                                // authenticated: navigate to Sessions and remove Login from backstack
                                try {
                                    navController.navigate(Screen.Sessions.route) {
                                        popUpTo(Screen.Login.route) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                } catch (e: Exception) {
                                    Log.e("MainActivity", "navigate to login failed")
                                }
                            }
                        }


                        LaunchedEffect(joinCode, authUiState.token) {
                            Log.d("MainActivity::joinCode", "$joinCode")

                            if (!joinCode.isNullOrEmpty()) {

                                if (authUiState.loading) return@LaunchedEffect

                                if (authUiState.token.isNullOrEmpty()) {
                                    // Not logged in -> redirect to login, and keep code for later
                                    sessionViewModel.setPendingInviteCode(joinCode)
                                    navController.navigate(Screen.Login.route)
                                } else {
                                    try {
                                        // go to join screen
                                        navController.navigate("${Screen.Join.route}/$joinCode")
                                    } catch (e: Exception) {
                                        Log.e("MainActivity", "Deep link nav failed")
                                    } finally {
                                        deepLinkFlow.value = null
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)

        val code = intent.data?.lastPathSegment
        deepLinkFlow.value = code
    }
}
package com.ochuko.tabsplit

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ochuko.tabsplit.ui.theme.TabSplitTheme
import androidx.navigation.compose.rememberNavController
import com.ochuko.tabsplit.store.AppStore
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.ochuko.tabsplit.store.AuthStore
import com.ochuko.tabsplit.ui.navigation.AppNavHost
import com.ochuko.tabsplit.ui.navigation.Screen
import android.util.Log
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
            TabSplitTheme {
                val navController = rememberNavController()

                // Shared AppStore instance
                val appStore: AppStore = viewModel()
                val authStore: AuthStore = viewModel()

                val authState by authStore.authState.collectAsState()

                // observe deep link
                val joinCode by deepLinkFlow.asStateFlow().collectAsState()

                // Wait for authStore to finish loading
                if (authState.loading) {
                    Box(
                        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    // Mount the NavHost (keeps your three-arg API)
                    AppNavHost(
                        navController,
                        appStore,
                        authStore
                    )

                    // Wait until appStore finishes loading
                    LaunchedEffect(authState.token) {
                        if (authState.token.isNullOrEmpty()) {
                            try {
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(0)
                                }
                            } catch (e: Exception) {
                                Log.w("MainActivity", "navigate to login failed")
                            }
                        } else {
                            // authenticated: navigate to Sessions and remove Login from backstack
                            try {
                                navController.navigate(Screen.Sessions.route) {
                                    popUpTo(Screen.Login.route) { inclusive = true }
                                    launchSingleTop = true
                                }
                            } catch (e: Exception) {
                                Log.w("MainActivity", "navigate to login failed")
                            }
                        }
                    }


                    LaunchedEffect(joinCode, authState.token) {
                        if (!joinCode.isNullOrEmpty()) {
                            try {
                                // go to join screen
                                navController.navigate("${Screen.Join.route}/$joinCode")
                            } catch (e: Exception) {
                                Log.w("MainActivity", "Deep link nav failed")
                            } finally {
                                deepLinkFlow.value = null
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
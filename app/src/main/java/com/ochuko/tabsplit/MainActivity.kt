package com.ochuko.tabsplit

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
import com.ochuko.tabsplit.ui.navigation.AppNavHost
import com.ochuko.tabsplit.ui.navigation.Screen


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TabSplitTheme {
                val navController = rememberNavController()
                val appStore: AppStore = viewModel()

                // Collect toke state
                val token by appStore.token.collectAsState(initial = null)
                var startDestination by remember { mutableStateOf<String?>(null) }

                // Wait until appStore finishes loading
                LaunchedEffect(token) {
                    startDestination = if (token.isNullOrEmpty()) {
                        Screen.Login.route
                    } else {
                        Screen.Sessions.route
                    }
                }

                if (startDestination != null) {
                    AppNavHost(navController = navController)
                } else {
                    // Loading screen
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {

                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}
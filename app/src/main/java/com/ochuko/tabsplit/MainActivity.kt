package com.ochuko.tabsplit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import com.ochuko.tabsplit.ui.theme.TabSplitTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ochuko.tabsplit.ui.join.JoinSessionScreen
import com.ochuko.tabsplit.ui.screens.auth.LoginScreen
import com.ochuko.tabsplit.ui.screens.auth.SignupScreen
import com.ochuko.tabsplit.ui.screens.sessions.SessionScreen
import com.ochuko.tabsplit.ui.screens.sessions.SessionDetailsScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TabSplitTheme {
                MyApp()
            }
        }
    }
}


@Composable
fun MyApp() {
    val navController = rememberNavController()

    Surface(color = MaterialTheme.colorScheme.background) {
        NavHost(navController = navController, startDestination = "login") {
            composable("login") {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate("session") {
                            popUpTo("Login") { inclusive = true }
                        }
                    },
                    onSignupClick = { navController.navigate("signup") })
            }

            composable("signup") {
                SignupScreen(
                    onSignupSuccess = {
                        navController.navigate("session") {
                            popUpTo("signup") { inclusive = true }
                        }
                    },
                    onLoginClick = { navController.navigate("login") }
                )
            }

            composable("sessions") {
                SessionScreen(
                    onSessionClick = { sessionId ->
                        navController.navigate("sessionDetails/$sessionId")
                    },
                    onCreateSession = { newSession ->
                        navController.navigate("sessionDetails/${newSession.id}")
                    },
                    onRequireAuth = {
                        // navigate to login
                        navController.navigate("LoginScreen")
                    }

                )
            }

            // Session Details
            composable("sessionDetails/{sessionId}") { backStackEntry ->
                val sessionId = backStackEntry.arguments?.getString("sessionId") ?: ""
                SessionDetailsScreen(navController, sessionId)
            }

            // Join Session
            composable("join/{inviteCode}") { backStackEntry ->
                val inviteCode = backStackEntry.arguments?.getString("inviteCode") ?: ""
                JoinSessionScreen(
                    inviteCode = inviteCode,
                    onJoinSuccess = { session ->
                        navController.navigate("sessionDetails/${session.id}") {
                            popUpTo("join/$inviteCode") { inclusive = true }
                        }
                    },
                    onAuthRequired = {
                        navController.navigate("login") {
                            popUpTo("join/$inviteCode") { inclusive = true }
                        }
                    },
                    onJoinFailed = {
                        navController.navigate("sessions") {
                            popUpTo("join/$inviteCode") { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}

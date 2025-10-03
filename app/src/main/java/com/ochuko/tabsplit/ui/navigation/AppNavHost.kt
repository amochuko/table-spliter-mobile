package com.ochuko.ui.navigation.tabsplit


import androidx.compose.runtime.Composable
import androidx.compose.runtime.internal.composableLambda
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.ochuko.tabsplit.store.AppStore
import com.ochuko.tabsplit.ui.auth.SignupScreen
import com.ochuko.tabsplit.ui.join.JoinSessionScreen
import com.ochuko.tabsplit.ui.screens.*
import com.ochuko.tabsplit.ui.screens.auth.LoginScreen
import com.ochuko.tabsplit.ui.screens.sessions.SessionDetailsScreen
import com.ochuko.tabsplit.ui.screens.sessions.SessionScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Signup : Screen("signup")
    object Sessions : Screen("sessions")
    object SessionDetails : Screen("sessionDetails/{sessionId}") {
        fun createRoute(sessionId: String) = "sessionDetails/$sessionId"
    }

    object Join : Screen("join")
}

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { navController.navigate((Screen.Sessions.route)) },
                onSignupClick = { navController.navigate(Screen.Signup.route) }
            )
        }

        composable(Screen.Signup.route) {
            SignupScreen(
                onSignupSuccess = { navController.navigate(Screen.Sessions.route) },
                onLoginClick = { navController.navigate(Screen.Login.route) }
            )
        }

        composable(Screen.Sessions.route) {
            SessionScreen(
                onRequireAuth = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Sessions.route) { inclusive = true }
                    }
                },
                onSessionClick = { sessionId ->
                    navController.navigate(
                        Screen.SessionDetails
                            .createRoute(sessionId)
                    )
                },
                onCreateSession = {
//                    TODO:  log event or refresh
                }
            )
        }

        composable(Screen.SessionDetails.route) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getString("sessionId")
            sessionId?.let {
                SessionDetailsScreen(navController, sessionId)
            }
        }

        composable(
            route = Screen.Join.route + "/{inviteCode}",
            arguments = listOf(navArgument("inviteCode") { type = NavType.StringType })
        ) { backStackEntry ->
            val inviteCode = backStackEntry.arguments?.getString("inviteCode") ?: ""

            JoinSessionScreen(
                inviteCode = inviteCode,
                onAuthRequired = { code ->
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Join.route) { inclusive = true }
                    }
                },
                onJoinSuccess = { sessionId ->
                    navController.navigate(
                        Screen.SessionDetails
                            .createRoute(sessionId.toString())
                    ) {
                        popUpTo(Screen.Join.route) { inclusive = true }
                    }
                },
                onJoinFailed = {
                    navController.navigate(Screen.Sessions.route) {
                        popUpTo(Screen.Join.route) { inclusive = true }
                    }
                }

            )
        }
    }
}


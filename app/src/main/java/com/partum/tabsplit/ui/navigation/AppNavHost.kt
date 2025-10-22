package com.ochuko.tabsplit.ui.navigation


import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.ochuko.tabsplit.ui.auth.AuthViewModel
import com.ochuko.tabsplit.ui.auth.SignupScreen
import com.ochuko.tabsplit.ui.join.JoinSessionScreen
import com.ochuko.tabsplit.ui.auth.LoginScreen
import com.ochuko.tabsplit.ui.session.SessionDetailsScreen
import com.ochuko.tabsplit.ui.session.SessionsScreen
import com.ochuko.tabsplit.ui.SplashScreen
import com.ochuko.tabsplit.ui.expense.ExpenseViewModel
import com.ochuko.tabsplit.ui.participant.ParticipantViewModel
import com.ochuko.tabsplit.ui.session.SessionViewModel

@Composable
fun AppNavHost(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    sessionViewModel: SessionViewModel,
    expenseViewModel: ExpenseViewModel,
    participantViewModel: ParticipantViewModel
) {

    val authUiState by authViewModel.uiState.collectAsState()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    Log.d("AppNavHost:user", authUiState.user?.email ?: "not user object")

    Scaffold(topBar = {
        if (authUiState.user != null && currentRoute != Screen.Login.route && currentRoute != Screen
                .Signup.route
        ) {

            MainTopBar(authViewModel, navController, onPersistZaddr = { z ->
                authViewModel
                    .updateZAddr(z)
            })
        }
    }) { innerPadding ->
        NavHost(
            navController, startDestination = Screen.Login.route, modifier = Modifier.padding
                (innerPadding)
        ) {

            composable(Screen.Splash.route) {
                SplashScreen(navController, authViewModel)
            }

            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Screen.Sessions.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onSignupClick = { navController.navigate(Screen.Signup.route) },
                    authViewModel,
                    sessionViewModel
                )
            }

            composable(Screen.Signup.route) {
                SignupScreen(
                    navController = navController,
                    onSignupSuccess = {
                        navController.navigate(Screen.Sessions.route) {
                            popUpTo(Screen.Sessions.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onLoginClick = { navController.navigate(Screen.Login.route) },
                    authViewModel,
                    sessionViewModel
                )
            }

            composable(Screen.Sessions.route) {
                SessionsScreen(
                    onSessionClick = { sessionId ->
                        navController.navigate(
                            Screen.SessionDetails
                                .createRoute(sessionId)
                        )
                    },
                    onCreateSession = {
//                    TODO:  log event or refresh
                    }, sessionViewModel
                )
            }

            composable(Screen.SessionDetails.route) { backStackEntry ->
                val sessionId = backStackEntry.arguments?.getString("sessionId")
                sessionId?.let {
                    SessionDetailsScreen(
                        navController,
                        sessionId,
                        sessionViewModel,
                        expenseViewModel,
                        participantViewModel
                    )
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
                    },
                    authViewModel = authViewModel,
                    sessionViewModel = sessionViewModel
                )
            }
        }
    }
}


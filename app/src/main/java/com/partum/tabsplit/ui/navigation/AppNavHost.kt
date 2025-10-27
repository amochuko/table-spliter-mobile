package com.partum.tabsplit.ui.navigation


import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.partum.tabsplit.R
import com.partum.tabsplit.ui.auth.AuthViewModel
import com.partum.tabsplit.ui.auth.SignupScreen
import com.partum.tabsplit.ui.join.JoinSessionScreen
import com.partum.tabsplit.ui.auth.LoginScreen
import com.partum.tabsplit.ui.session.SessionDetailsScreen
import com.partum.tabsplit.ui.session.SessionsScreen
import com.partum.tabsplit.ui.SplashScreen
import com.partum.tabsplit.ui.expense.ExpenseViewModel
import com.partum.tabsplit.ui.participant.ParticipantViewModel
import com.partum.tabsplit.ui.session.SessionViewModel

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

    val canNavigateBack = navController.previousBackStackEntry != null

    val title = when {
        currentRoute?.startsWith(Screen.SessionDetails.route) == true -> stringResource(R.string.session_details)
        currentRoute?.startsWith(Screen.Join.route) == true -> stringResource(R.string.join_session)
        currentRoute == Screen.Sessions.route -> stringResource(R.string.sessions)
        else -> stringResource(R.string.tablesplit)
    }

    Scaffold(
        topBar = {
            if (authUiState.user != null &&
                currentRoute != Screen.Login.route &&
                currentRoute != Screen.Signup.route
            ) {

                MainTopBar(
                    authViewModel,
                    navController,
                    onPersistZaddr = { z ->
                        authViewModel
                            .updateZAddr(z)
                    },
                    canNavigateBack = canNavigateBack,
                    title = title
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController,
            startDestination = Screen.Login.route,
            modifier = Modifier.padding(innerPadding)
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
                        participantViewModel,
                        authUiState
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
                            popUpTo(Screen.Join.route) { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    onJoinFailed = {
                        navController.navigate(Screen.Sessions.route) {
                            popUpTo(0)
                            launchSingleTop = true
                        }
                    },
                    authViewModel = authViewModel,
                    sessionViewModel = sessionViewModel
                )
            }
        }
    }
}


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
import com.ochuko.tabsplit.store.AppStore
import com.ochuko.tabsplit.store.AuthStore
import com.ochuko.tabsplit.ui.screens.auth.SignupScreen
import com.ochuko.tabsplit.ui.screens.join.JoinSessionScreen
import com.ochuko.tabsplit.ui.screens.auth.LoginScreen
import com.ochuko.tabsplit.ui.screens.sessions.SessionDetailsScreen
import com.ochuko.tabsplit.ui.screens.sessions.SessionsScreen
import com.ochuko.tabsplit.ui.screens.splash.SplashScreen


@Composable
fun AppNavHost(navController: NavHostController, appStore: AppStore, authStore: AuthStore) {

    val authState by authStore.authState.collectAsState()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route


    Scaffold(topBar = {
        if (authState.user != null && currentRoute != Screen.Login.route && currentRoute != Screen
                .Signup.route
        ) {

            MainTopBar(authStore, navController, onPersistZaddr = { z ->
                authStore
                    .updateZAddr(z)
            })
        }
    }) { innerPadding ->
        NavHost(
            navController, startDestination = Screen.Login.route, modifier = Modifier.padding
                (innerPadding)
        ) {

            composable(Screen.Splash.route) {
                SplashScreen(navController, appStore)
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
                    appStore = appStore,
                    authStore = authStore
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
                    appStore = appStore,
                    authStore = authStore
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
                    }, appStore
                )
            }

            composable(Screen.SessionDetails.route) { backStackEntry ->
                val sessionId = backStackEntry.arguments?.getString("sessionId")
                sessionId?.let {
                    SessionDetailsScreen(navController, sessionId, appStore)
                }
            }

            composable(
                route = Screen.Join.route + "/{inviteCode}",
                arguments = listOf(navArgument("inviteCode") { type = NavType.StringType })
            ) { backStackEntry ->
                val inviteCode = backStackEntry.arguments?.getString("inviteCode") ?: ""

                JoinSessionScreen(
                    appStore,
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
}


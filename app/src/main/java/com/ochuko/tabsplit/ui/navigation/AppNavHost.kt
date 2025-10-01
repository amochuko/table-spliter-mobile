package com.ochuko.ui.navigation.tabsplit


import androidx.compose.runtime.Composable
import androidx.compose.runtime.internal.composableLambda
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.tabsplit.ui.screens.*

sealed class Screen(val route :String){
    object  Login: Screen("login")
    object Signup : Screen("signup")
    object Sessions: Screen("sessions")
    object SessionDetails: Screen("sessionDetails/{sessionId}"){
        fun createRoute(sessionId:String) = "sessionDetails/$sessionId"
    }
    object  Join: Screen("join")
}

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Login.route){
        composable(Screen.Login.route){
            LoginScreen(
                onLoginSuccess = {navController.navigate(Screen.Sessions.route)},
                onSignupClick = {navController.navigate(Screen.Signup.route)}
            )
        }

        composableLambda(Screen.Signup.route){
            SignupScreen(
                onSignupSuccess = {navController.navigate(Screen.Sessions.route)}
            )
        }

        composable(Screen.Sessions.route) {
            SessionsScreen(
                onSessionClick = { sessionId ->
                    navController.navigate(Screen.SessionDetails.createRoute(sessionId))
                }
            )
        }

        composable(Screen.SessionDetails.route) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getString("sessionId")
            sessionId?.let {
                SessionDetailsScreen(
                    sessionId = it,
                    onBack = { navController.popBackStack() }
                )
            }
        }

        composable(Screen.Join.route) {
            JoinScreen(onJoinSuccess = { navController.navigate(Screen.Sessions.route) })
        }
    }
}


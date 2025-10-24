package com.partum.tabsplit.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Signup : Screen("signup")
    object Sessions : Screen("sessions")
    object SessionDetails : Screen("sessionDetails/{sessionId}") {
        fun createRoute(sessionId: String) = "sessionDetails/$sessionId"
    }

    object Splash : Screen("splash")
    object Join : Screen("join")
    object Profile : Screen("profile")
}

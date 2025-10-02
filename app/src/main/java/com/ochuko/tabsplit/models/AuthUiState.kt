package com.ochuko.tabsplit.models


data class AuthUiState(
    val token: String? = null,
    val isLoggedIn: Boolean = false,
    val error: String? = null
)

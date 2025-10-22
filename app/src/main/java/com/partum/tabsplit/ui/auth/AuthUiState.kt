package com.ochuko.tabsplit.ui.auth

import com.ochuko.tabsplit.data.model.User

data class AuthUiState(
    val token: String? = null,
    val user: User? = null,
    val isLoggedIn: Boolean = false,
    val error: String? = null,
    val loading: Boolean = false,
)
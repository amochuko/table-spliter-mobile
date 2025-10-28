package com.partum.tabsplit.ui.auth

import com.partum.tabsplit.data.model.User

data class AuthUiState(
    val token: String? = null,
    val user: User? = null,
    val isLoggedIn: Boolean = false,
    val error: String? = null,
    val loading: Boolean = false,
    val isSaving: Boolean = false,
)
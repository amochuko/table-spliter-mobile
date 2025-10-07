package com.ochuko.tabsplit.models

data class AuthState(
    val user: User? = null,
    val token: String? = null,
    val loading: Boolean = true
)

package com.ochuko.tabsplit.models

data class User(
    val id: String,
    val username: String,
    val email: String,
    val zaddr: String?
)

data class UserToken (val user: User, val token: String)
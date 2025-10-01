package com.ochuko.tabsplit.models

data class Expense(
    val id: String,
    val sessionId: String,
    val memo: String,
    val amount: Double,
    val payerId: String,
    val createdAt: String,
)



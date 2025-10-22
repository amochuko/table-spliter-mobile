package com.ochuko.tabsplit.ui.expense

import com.ochuko.tabsplit.data.model.Expense

data class ExpenseUiState(
    val expenses: Map<String, List<Expense>> = emptyMap(),
    val loading: Boolean = false,
    val error: String? = null,
)
package com.partum.tabsplit.ui.expense

import com.partum.tabsplit.data.model.Expense

data class ExpenseUiState(
    val expenses: Map<String, List<Expense>> = emptyMap(),
    val loading: Boolean = false,
    val error: String? = null,
)
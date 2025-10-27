package com.partum.tabsplit.data.model

data class SessionWithExpensesAndParticipants(
    val session: Session,
    val participants: List<Participant> = emptyList(),
    val expenses: List<Expense> = emptyList(),
)
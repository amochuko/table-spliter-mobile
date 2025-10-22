package com.partum.tabsplit.utils

import com.partum.tabsplit.data.model.Expense
import com.partum.tabsplit.data.model.Participant

fun calculateBalances(
    sessionId: String,
    participants: Map<String, List<Participant>>,
    expenses: Map<String, List<Expense>>
): Map<String, Double> {
    val balances = mutableMapOf<String, Double>()
    val sessionParticipants = participants[sessionId].orEmpty()
    val sessionExpenses = expenses[sessionId].orEmpty()

    // Init everyone to 0
    sessionParticipants.forEach { participant -> balances[participant.id] = 0.0 }

    // Apply each expense
    sessionExpenses.forEach { expense ->
        val amount = expense.amount.toDouble()
        val share =
            if (sessionParticipants.isNotEmpty()) amount / sessionParticipants.size else 0.0

        sessionParticipants.forEach { participant ->
            balances[participant.id] = balances[participant.id]?.let { current ->
                if (participant.id == expense.payerId) {
                    current + amount - share
                } else {
                    current - share
                }
            } ?: 0.0
        }
    }

    return balances
}
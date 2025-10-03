package com.ochuko.tabsplit.utils

import com.ochuko.tabsplit.models.Expense
import com.ochuko.tabsplit.models.Participant

fun calculateBalances(
    sessionId: String,
    participants: Map<String, List<Participant>>,
    expenses: Map<String, List<Expense>>
): Map<String, Double> {
    val balances = mutableMapOf<String, Double>()
    val sessionParticipants = participants[sessionId].orEmpty()
    val sessionExpenses = expenses[sessionId].orEmpty()

    // Init everyone to 0
    sessionParticipants.forEach { balances[it.id] = 0.0 }

    // Apply each expense
    sessionExpenses.forEach { e ->
        val amount = e.amount.toDouble()
        val share =
            if (sessionParticipants.isNotEmpty()) amount / sessionParticipants.size else 0.0

        sessionParticipants.forEach { p ->
            balances[p.id] = balances[p.id]?.let { current ->
                if (p.id == e.payerId) current + amount - share else current - share
            } ?: 0.0
        }
    }

    return balances
}
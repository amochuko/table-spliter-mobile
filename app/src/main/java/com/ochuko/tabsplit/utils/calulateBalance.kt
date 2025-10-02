package com.ochuko.tabsplit.utils

import com.ochuko.tabsplit.models.Expense

typealias Balances = MutableMap<String, Double>

fun calculateBalances(expenses:List<Expense>,participants: List<String> ): Balances{
    val balances: Balances = mutableMapOf();

    participants.forEach { balances[it]=0.0 }

val total = expenses.sumOf { it.amount }
    val share = if (participants.isNotEmpty()) total / participants.size else 0.0

    for(expense in expenses){
        balances[expense.payerId] = (balances[expense.payerId]?: 0.0)+ expense.amount
    }

    for(part in participants){
        balances[part]= (balances[part]) ?: (0.0 - share)
    }

    return balances
}


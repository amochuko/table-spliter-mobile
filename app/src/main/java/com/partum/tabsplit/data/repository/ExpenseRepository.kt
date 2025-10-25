package com.partum.tabsplit.data.repository

import com.partum.tabsplit.data.api.AddExpenseResponse
import com.partum.tabsplit.data.model.AddExpenseRequest
import android.util.Log
import com.partum.tabsplit.data.api.ExpenseApi
import com.partum.tabsplit.data.model.Expense

interface IExpenseRepo {
    suspend fun getExpenses(sessionId: String): List<Expense>
    suspend fun addExpenses(sessionId: String, req: AddExpenseRequest): AddExpenseResponse?
    suspend fun deleteExpense(sessionId: String, expenseId: String)
}

class ExpenseRepository(private val api: ExpenseApi) : IExpenseRepo {
    override suspend fun getExpenses(sessionId: String): List<Expense> {
        val res = api.getExpenses(sessionId)

        if (res.isSuccessful) {
            return res.body()?.expenses ?: emptyList()
        }

        throw Exception("Failed to fetch expenses: ${res.code()} -> ${res.errorBody()}")
    }

    override suspend fun addExpenses(
        sessionId: String,
        req: AddExpenseRequest
    ): AddExpenseResponse? {
        TODO("Not yet implemented")
    }

    override suspend fun deleteExpense(sessionId: String, expenseId: String) {
        TODO("Not yet implemented")
    }
}



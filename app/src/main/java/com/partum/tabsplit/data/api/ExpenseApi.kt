package com.partum.tabsplit.data.api

import com.partum.tabsplit.data.model.Expense
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

data class ExpensesResponse(
    val expenses: List<Expense>?
)

data class ExpenseResponse(
    val expense: Expense
)

interface ExpenseApi {
    @GET("/expenses/{sessionId}")
    suspend fun getExpenses(@Path("sessionId") sessionId: String): Response<ExpensesResponse>

    @GET("/expenses/{sessionId}/{id}")
    suspend fun getExpense(
        @Path("id") sessionId: String,
        @Path("id") id: String
    ): Response<ExpenseResponse>
}

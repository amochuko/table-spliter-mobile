package com.ochuko.tabsplit.data.api

import com.google.gson.annotations.SerializedName
import com.ochuko.tabsplit.data.model.Expense
import com.ochuko.tabsplit.data.model.Participant
import com.ochuko.tabsplit.data.model.Session
import retrofit2.Response
import retrofit2.http.*
import com.ochuko.tabsplit.data.model.AddExpenseRequest

data class SessionsResponse(
    val sessions: List<Session>?
)

data class SessionOwner(
    val id: String,
    val username: String,
    val zaddr: String,

    @SerializedName("user_id")
    val userId: String,
)

data class SessionWithOwner(
    val id: String,
    val title: String,
    val description: String,
    val currency: String,

    @SerializedName("created_at")
    val createdAt: String,

    val owner: SessionOwner,
)

data class SessionOwnerResponse(
    val participants: List<Participant>,
    val expenses: List<Expense>,
    val session: SessionWithOwner
)

data class SessionResponse(
    val session: Session
)

data class AddExpenseResponse(
    val sessionId: String,
    val participants: List<Participant>,
    val expenses: List<Expense>
)

data class SessionRequest(val title: String, val description: String?)

data class JoinRequest(val inviteCode: String)

interface SessionApi {
    @GET("/sessions")
    suspend fun getSessions(): Response<SessionsResponse>

    @GET("/sessions/{id}")
    suspend fun getSession(@Path("id") id: String): Response<SessionOwnerResponse>

    @POST("/sessions")
    suspend fun createSession(@Body body: SessionRequest): Response<SessionResponse>

    @POST("/sessions/join")
    suspend fun joinByInvite(@Body body: JoinRequest): Response<Session>

    @POST("/sessions/{id}/expenses")
    suspend fun addExpenses(@Path("id") id: String, @Body body: AddExpenseRequest):
            Response<AddExpenseResponse>
}

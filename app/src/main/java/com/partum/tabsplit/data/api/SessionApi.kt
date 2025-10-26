package com.partum.tabsplit.data.api

import com.google.gson.annotations.SerializedName
import com.partum.tabsplit.data.model.Expense
import com.partum.tabsplit.data.model.Participant
import com.partum.tabsplit.data.model.Session
import retrofit2.Response
import retrofit2.http.*
import com.partum.tabsplit.data.model.AddExpenseRequest
import com.partum.tabsplit.data.model.SessionWithExpensesAndParticipants

data class SessionsResponse(
    val ownedSessions: List<Session>?,
    val joinedSessions: List<Session>?
)

data class SessionWithExpensesAndParticipantsResponse(
    @SerializedName("sessionById")
    val sessionWithExpensesAndParticipants: SessionWithExpensesAndParticipants?
)

data class SessionResponse(
    val session: Session
)

data class AddExpenseResponse(
    val sessionId: String,
    val participants: List<Participant>,
    val expenses: List<Expense>
)

data class SessionRequest(
    val title: String,
    val description: String,
    val startDateTime: String,
    val endDateTime: String
)

data class JoinRequest(val inviteCode: String)

interface SessionApi {
    @GET("/sessions")
    suspend fun getSessions(): Response<SessionsResponse>

    @GET("/sessions/{id}")
    suspend fun getSession(@Path("id") id: String): Response<SessionWithExpensesAndParticipantsResponse>

    @POST("/sessions")
    suspend fun createSession(@Body body: SessionRequest): Response<SessionResponse>

    @POST("/sessions/join")
    suspend fun joinByInvite(@Body body: JoinRequest): Response<Session>

    @POST("/sessions/{id}/expenses")
    suspend fun addExpenses(@Path("id") id: String, @Body body: AddExpenseRequest):
            Response<AddExpenseResponse>
}

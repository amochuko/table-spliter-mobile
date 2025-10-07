package com.ochuko.tabsplit.data.api

import com.ochuko.tabsplit.models.Session
import retrofit2.Response
import retrofit2.http.*


data class SessionsResponse(
    val sessions: List<Session>?
)

data class SessionResponse( val session: Session?)

data class SessionRequest(val title: String, val description: String?)

data class JoinRequest(val inviteCode: String)

interface SessionApi {
    @GET("/sessions")
    suspend fun getSessions(): Response<List<Session>>

    @GET("/sessions/{id}")
    suspend fun getSession(@Path("id") id: String): Response<Session>

    @POST("/sessions")
    suspend fun createSession(@Body body: SessionRequest): Response<Session>

    @POST("/sessions/join")
    suspend fun joinByInvite(@Body body: JoinRequest): Response<Session>

}

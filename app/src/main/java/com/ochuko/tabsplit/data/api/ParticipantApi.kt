package com.ochuko.tabsplit.data.api

import com.ochuko.tabsplit.data.model.Participant
import retrofit2.http.GET
import retrofit2.Response

data class ParticipantsResponse(
    val participants: List<Participant>?
)

data class ParticipantResponse(
    val participant: Participant?
)

interface ParticipantApi {
    @GET("/participants")
    suspend fun getParticipants() : Response<ParticipantsResponse>

}
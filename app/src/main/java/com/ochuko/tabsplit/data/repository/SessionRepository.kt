package com.ochuko.tabsplit.data.repository

import android.util.Log
import com.ochuko.tabsplit.data.api.JoinRequest
import com.ochuko.tabsplit.data.api.SessionRequest
import com.ochuko.tabsplit.data.api.SessionApi
import com.ochuko.tabsplit.models.Session

class SessionRepository(private val api: SessionApi) {

    suspend fun getSessions(): List<Session> {
        val response = api.getSessions()

        if (response.isSuccessful) return response.body() ?: emptyList()
        throw Exception("Failed to fetch sessions: ${response.code()}")
    }

    suspend fun getSession(id: String): Session? {
        val res = api.getSession(id)
        return if (res.isSuccessful) res.body() else null
    }

    suspend fun createSession(req: SessionRequest): Session? {
        Log.i("SessionRepository1", "Request: $req")

        val res = api.createSession(req)

        Log.i("SessionRepository2", "Code: ${res.code()} | Success: ${res.isSuccessful}")
        Log.i("SessionRepository3", "Body: ${res.body()}")
        Log.i("SessionRepository4", "ErrorBody: ${res.errorBody()?.string()}")
        Log.i("SessionRepository5", "Raw: ${res.raw()}")

        return if (res.isSuccessful) res.body() else null
    }

    suspend fun joinByInvite(code: String): Session? {
        val res = api.joinByInvite(JoinRequest(code))
        return if (res.isSuccessful) res.body() else null
    }
}
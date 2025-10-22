package com.partum.tabsplit.data.repository

import com.partum.tabsplit.data.api.AddExpenseResponse
import com.partum.tabsplit.data.api.JoinRequest
import com.partum.tabsplit.data.api.SessionRequest
import com.partum.tabsplit.data.api.SessionApi
import com.partum.tabsplit.data.api.SessionOwnerResponse
import com.partum.tabsplit.data.model.AddExpenseRequest
import com.partum.tabsplit.data.model.Session
import android.util.Log

class SessionRepository(private val api: SessionApi) {

    suspend fun getSessions(): List<Session> {
        val res = api.getSessions()

        if (res.isSuccessful) return res.body()?.sessions ?: emptyList()

        throw Exception("Failed to fetch sessions: ${res.code()} -> ${res.errorBody()}")
    }

    suspend fun getSession(id: String): SessionOwnerResponse? {
        val res = api.getSession(id)
        return if (res.isSuccessful) res.body() else null
    }

    suspend fun createSession(req: SessionRequest): Session? {

        val res = api.createSession(req)

        return if (res.isSuccessful) res.body()?.session else null
    }

    suspend fun addExpenses(sessionId: String, req: AddExpenseRequest): AddExpenseResponse? {

        val res = api.addExpenses(sessionId, req)
        Log.d("SessionRepo:addExpenses", "Code: ${res.code()}, Body: ${res.body()}")

        return if (res.isSuccessful) res.body() else null
    }

    suspend fun joinByInvite(code: String): Session? {
        val res = api.joinByInvite(JoinRequest(code))
        return if (res.isSuccessful) res.body() else null
    }
}
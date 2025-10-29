package com.partum.tabsplit.data.repository

import com.partum.tabsplit.data.api.AddExpenseResponse
import com.partum.tabsplit.data.api.JoinRequest
import com.partum.tabsplit.data.api.SessionRequest
import com.partum.tabsplit.data.api.SessionApi
import com.partum.tabsplit.data.api.SessionWithExpensesAndParticipantsResponse
import com.partum.tabsplit.data.model.AddExpenseRequest
import com.partum.tabsplit.data.model.Session
import android.util.Log
import com.partum.tabsplit.data.api.SessionsResponse

interface ISessionRepo {
    suspend fun getSessions(): SessionsResponse
    suspend fun getSession(id: String): SessionWithExpensesAndParticipantsResponse?
    suspend fun createSession(req: SessionRequest): Session?
    suspend fun addExpenses(sessionId: String, req: AddExpenseRequest): AddExpenseResponse?
    suspend fun joinByInvite(code: String): Session?
    suspend fun deleteSession(sessionId: String): Boolean
}

class SessionRepository(private val api: SessionApi) : ISessionRepo {

    override suspend fun getSessions(): SessionsResponse {
        val res = api.getSessions()

        if (res.isSuccessful) return res.body()!!

        throw Exception("Failed to fetch sessions: ${res.code()} -> ${res.errorBody()}")
    }

    override suspend fun getSession(id: String): SessionWithExpensesAndParticipantsResponse? {
        val res = api.getSession(id)
        return if (res.isSuccessful) res.body() else null
    }

    override suspend fun createSession(req: SessionRequest): Session? {

        val res = api.createSession(req)

        return if (res.isSuccessful) res.body()?.session else null
    }

    override suspend fun addExpenses(sessionId: String, req: AddExpenseRequest):
            AddExpenseResponse? {

        val res = api.addExpenses(sessionId, req)
        Log.d("SessionRepo:addExpenses", "Code: ${res.code()}, Body: ${res.body()}")

        return if (res.isSuccessful) res.body() else null
    }

    override suspend fun joinByInvite(code: String): Session? {
        val res = api.joinByInvite(JoinRequest(code))

        Log.d("SessionRepo:joinByInvite", "Code: ${res.code()}, Body: ${res.body()}")
        return if (res.isSuccessful) res.body() else null
    }

    override suspend fun deleteSession(sessionId: String): Boolean {
        val res = api.deleteSession(sessionId)

        return (if(res.isSuccessful) res.body() else false) == true
    }
}
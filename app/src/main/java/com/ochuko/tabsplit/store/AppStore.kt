package com.ochuko.tabsplit.store

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.ochuko.tabsplit.data.api.ApiClient
import com.ochuko.tabsplit.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import androidx.lifecycle.AndroidViewModel
import com.ochuko.tabsplit.utils.Config
import com.ochuko.tabsplit.data.api.SessionApi
import com.ochuko.tabsplit.data.api.SessionRequest
import com.ochuko.tabsplit.data.repository.SessionRepository


const val BASE_URL = Config.API_BASE_URL

class AppStore(
    app: Application
) : AndroidViewModel(app) {

    private val authStore = AuthStore(app)

    // APIs
    private val sessionApi = ApiClient.create<SessionApi>(token = authStore.getToken(), BASE_URL)

    // Repo
    private val sessionRepo = SessionRepository(sessionApi)

    // -- State
    private val _token = MutableStateFlow<String?>(null);
    val token: StateFlow<String?> = _token

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _sessions = MutableStateFlow<List<Session>>(emptyList())
    val sessions: StateFlow<List<Session>> = _sessions

    private val _participants = MutableStateFlow<Map<String, List<Participant>>>(emptyMap())
    val participants: StateFlow<Map<String, List<Participant>>> = _participants

    private val _expenses = MutableStateFlow<Map<String, List<Expense>>>(emptyMap())
    val expenses: StateFlow<Map<String, List<Expense>>> = _expenses

    private val _pendingInviteCode = MutableStateFlow<String?>(null)
    val pendingInviteCode: StateFlow<String?> = _pendingInviteCode


    init {
        viewModelScope.launch {
            authStore.authState.collect { state ->
                Log.d(
                    "AppStore",
                    "Auth state updated: token=${state.token}, loading=${state.loading}"
                )

                if (state.token != null && !state.loading) {
                    _token.value = state.token

                    loadSessions()
                }
            }
        }
    }

    fun loadSessions() = viewModelScope.launch {
        try {
            val newSessions = sessionRepo.getSessions()
            _sessions.value = newSessions
        } catch (e: Exception) {
            Log.e("AppStore", "loadSessions failed", e)
        }
    }

    fun setPendingInviteCode(code: String?) {
        _pendingInviteCode.value = code
    }

    fun addExpense(sessionId: String, expense: Expense) {
        val current = _expenses.value.toMutableMap()
        val updatedList = current[sessionId].orEmpty() + expense
        current[sessionId] = updatedList
        _expenses.value = current
    }

    fun deleteExpense(sessionId: String, expenseId: String) {
        val current = _expenses.value.toMutableMap();
        val updatedList = current[sessionId].orEmpty().filterNot { it.id == expenseId }
        current[sessionId] = updatedList
        _expenses.value = current
    }

    // --- Sessions ---
    fun setSession(session: List<Session>) {
        _sessions.value = session
    }

    fun addSession(session: Session) {
        _sessions.value = _sessions.value + session
    }

    fun deleteSession(sessionId: String) {
        _sessions.value = _sessions.value.filterNot { it.id == sessionId }
    }

    suspend fun createSession(title: String, description: String?): Session? {

        return try {
            val session = sessionRepo.createSession(SessionRequest(title, description))

            session?.also {
                _sessions.value = _sessions.value + it
            }

        } catch (e: Exception) {
            Log.e("AppStore", "createSession failed", e)
            null
        }
    }

    suspend fun joinSessionByInvite(code: String): Session? {
        return try {
            val session = sessionRepo.joinByInvite(code)
            session?.also { _sessions.value = _sessions.value + it }

        } catch (e: Exception) {
            Log.e("AppStore", "joinSession failed", e)
            null
        }
    }

    fun fetchSession(sessionId: String) = viewModelScope.launch {
        // TODO: fetch single session details
    }


}
package com.ochuko.tabsplit.store

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ochuko.tabsplit.data.api.ApiClient
import com.ochuko.tabsplit.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.content.Context
import com.ochuko.tabsplit.BuildConfig
import com.ochuko.tabsplit.data.api.AuthApi
import com.ochuko.tabsplit.data.api.SessionApi
import com.ochuko.tabsplit.data.api.SessionRequest
import com.ochuko.tabsplit.data.repository.AuthRepository
import com.ochuko.tabsplit.data.repository.SessionRepository

const val BASE_URL = BuildConfig.API_BASE_URL

class AppStore(ctx: Context) : ViewModel() {

    // APIs
    private val sessionApi = ApiClient.create<SessionApi>(ctx, BASE_URL)
    private val authApi = ApiClient.create<AuthApi>(ctx, BASE_URL)

    // Repo
    private val sessionRepo = SessionRepository(sessionApi)
    private val authRepo = AuthRepository(authApi, ctx)


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

    fun setPendingInviteCode(code: String?) {
        _pendingInviteCode.value = code
    }

    fun setUser(user: User, token: String) {
        _user.value = user
        _token.value = token
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

    fun loadSessions() = viewModelScope.launch {
        try {
            val newSessions = sessionRepo.getSessions()
            _sessions.value = newSessions
        } catch (e: Exception) {
            Log.e("AppStore", "loadSessions failed", e)
        }
    }

    suspend fun createSession(title: String, description: String): Session? {
        return try {
            val session = sessionRepo.createSession(SessionRequest(title, description))
            session?.also { _sessions.value = _sessions.value + it }

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
        // TODO: fetch single sesssion details
    }


    // --- Auth ---
    fun login(email: String, password: String) = viewModelScope.launch {
        try {

            authRepo.login(email, password)?.let { (user, token) ->
                // This block is only executed if the login was successful
                // You can use the user and token variables here
                _user.value = user
                _token.value = token


                loadSessions()
            } ?: run {
                // This block is executed if loginResult is null
                // Handle the failed
                throw IllegalStateException("Login failed!")
            }


        } catch (e: Exception) {
            Log.e("AppStore", "Login failed", e)
        }
    }

    fun signup(email: String, password: String) = viewModelScope.launch {
        try {
            val u = authRepo.signup(email, password)
            if (u != null) {
                val (user, token) = u
                _user.value = user
                _token.value = token
            }
        } catch (e: Exception) {
            Log.e("AppStore", "Register failed", e)
        }
    }

}
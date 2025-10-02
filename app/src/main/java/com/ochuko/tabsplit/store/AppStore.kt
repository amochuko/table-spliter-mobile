package com.ochuko.tabsplit.store

import android.util.Log
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ochuko.tabsplit.data.api.ApiClient
import com.ochuko.tabsplit.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.app.Application
import android.content.Context
import com.ochuko.tabsplit.data.api.AuthApi
import com.ochuko.tabsplit.data.api.SessionApi
import com.ochuko.tabsplit.data.api.SessionRequest
import com.ochuko.tabsplit.data.repository.AuthRepository
import com.ochuko.tabsplit.data.repository.SessionRepository
import kotlinx.coroutines.flow.update

const val BASE_URL = "http://10.0.0.2:4000"

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

    fun createSession(title: String, description: String) = viewModelScope.launch {
        try {
            val session = sessionRepo.createSession(SessionRequest(title, description))
            session?.let { _sessions.value = _sessions.value + it }
        } catch (e: Exception) {
            Log.e("AppStore", "createSession failed", e)
        }
    }

    fun joinSession(code: String) = viewModelScope.launch {
        try {
            val session = sessionRepo.joinByInvite(code)
            session?.let { _sessions.value = _sessions.value + it }

        } catch (e: Exception) {
            Log.e("AppStore", "joinSession failed", e)
        }
    }

    fun fetchSession(sessionId: String) = viewModelScope.launch {
        // TODO: fetch single sesssion details
    }


    // --- Auth ---
    fun login(email: String, password: String) = viewModelScope.launch {
        try {
            val u = authRepo.login(email, password)
            if (u != null) {
                _user.value = u
                _token.value = u.token
            }
        } catch (e: Exception) {
            Log.e("AppStore", "Login failed", e)
        }
    }

    fun signup(email: String, password: String) = viewModelScope.launch {
        try {
            val u = authRepo.register(email, password)
            if (u != null) {
                _user.value = u
                _token.value = u.token
            }
        } catch (e: Exception) {
            Log.e("AppStore", "Register failed", e)
        }
    }

}
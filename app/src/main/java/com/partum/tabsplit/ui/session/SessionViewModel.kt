package com.partum.tabsplit.ui.session

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.partum.tabsplit.data.api.SessionRequest
import com.partum.tabsplit.data.model.FullSession
import com.partum.tabsplit.data.model.toFullSession
import com.partum.tabsplit.data.repository.SessionRepository
import com.partum.tabsplit.ui.auth.AuthViewModel
import com.partum.tabsplit.ui.expense.ExpenseViewModel
import com.partum.tabsplit.ui.participant.ParticipantViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SessionViewModel(
    private val sessionRepo: SessionRepository,
    private val authViewModel: AuthViewModel,
    private val participantViewModel: ParticipantViewModel,
    private val expenseViewModel: ExpenseViewModel
) : ViewModel() {

    private val _uiState = MutableStateFlow(SessionUiState())
    val uiState: StateFlow<SessionUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            authViewModel.uiState.collect { state ->
                if (state.token != null && !state.loading) {
                    loadSessions()
                }
            }
        }
    }

    fun loadSessions() = viewModelScope.launch {
        _uiState.update { it.copy(loading = true) }

        try {
            val sessions = sessionRepo.getSessions()
            val mapped = sessions.map { it.toFullSession() }

            _uiState.update {
                it.copy(
                    sessions = mapped,
                    loading = false
                )
            }
        } catch (e: Exception) {
            _uiState.update { it.copy(error = e.message, loading = false) }
        }
    }

    suspend fun createSession(title: String, description: String?): FullSession? {
        return try {
            val response = sessionRepo.createSession(SessionRequest(title, description))

            response?.let {
                _uiState.update { s ->
                    s.copy(sessions = s.sessions + it.toFullSession())
                }
            }

            response?.toFullSession()
        } catch (e: Exception) {
            _uiState.update { it.copy(error = e.message) }
            null
        }
    }

    suspend fun joinSessionByInvite(code: String): FullSession? {
        return try {
            val joinedSession = sessionRepo.joinByInvite(code)

            joinedSession?.let { it ->
                _uiState.update { s -> s.copy(sessions = s.sessions + it.toFullSession()) }
            }

            joinedSession?.toFullSession()
        } catch (e: Exception) {
            _uiState.update { it -> it.copy(error = e.message) }
            null
        }
    }

    fun deleteSession(sessionId: String) = viewModelScope.launch {
        _uiState.update {
            it.copy(sessions = it.sessions.filterNot { s -> s.id == sessionId })
        }
    }

    fun setPendingInviteCode(code: String?) {
        _uiState.update { it.copy(pendingInviteCode = code) }
    }

    fun fetchSession(sessionId: String): Any? = viewModelScope.launch {
        _uiState.update { it.copy(loading = true) }
        try {
            val response = sessionRepo.getSession(sessionId)
            response?.let { res ->

                _uiState.update {
                    it.copy(
                        loading = false,
                        sessions = (it.sessions + response.session.toFullSession()).distinctBy { s ->
                            s.id
                        }
                    )
                }

                participantViewModel.updateParticipants(sessionId, res.participants)
                expenseViewModel.updateExpenses(sessionId, res.expenses)
            }

            return@launch

        } catch (e: Exception) {
            Log.e("SessionViewModel", "fetchSession failed", e)

            _uiState.update {
                it.copy(
                    loading = false,
                    error = e.message
                )
            }
            null
        }
    }
}
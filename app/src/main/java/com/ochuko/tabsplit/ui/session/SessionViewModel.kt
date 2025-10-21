package com.ochuko.tabsplit.ui.session

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ochuko.tabsplit.data.api.SessionOwner
import com.ochuko.tabsplit.data.api.SessionOwnerResponse
import com.ochuko.tabsplit.data.api.SessionRequest
import com.ochuko.tabsplit.data.api.SessionWithOwner
import com.ochuko.tabsplit.data.model.Participant
import com.ochuko.tabsplit.data.model.toSessionWithOwner
import com.ochuko.tabsplit.data.repository.SessionRepository
import com.ochuko.tabsplit.ui.auth.AuthViewModel
import com.ochuko.tabsplit.ui.expense.ExpenseViewModel
import com.ochuko.tabsplit.ui.participant.ParticipantViewModel
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
                    loadSession()
                }
            }
        }
    }

    fun loadSession() = viewModelScope.launch {
        _uiState.update { it.copy(loading = true) }

        try {
            val sessions = sessionRepo.getSessions()

            // Convert to SessionWithOwner placeholders (owner minimal)
            val user = authViewModel.uiState.value.user

            val sessionWithOwner = sessions.map { s ->
                SessionWithOwner(
                    id = s.id,
                    title = s.title,
                    description = s.description,
                    currency = s.currency,
                    createdAt = s.createdAt.toString(),
                    owner = SessionOwner(
                        id = s.id,
                        username = s.createdBy,
                        zaddr = user?.zaddr!!,
                        userId = s.createdBy
                    )
                )
            }

            _uiState.update {
                it.copy(
                    sessions = sessionWithOwner,
                    loading = false
                )
            }
        } catch (e: Exception) {
            _uiState.update { it.copy(error = e.message, loading = false) }
        }
    }

    fun createSession(title: String, description: String?) = viewModelScope.launch {
        try {
            sessionRepo.createSession(SessionRequest(title, description))?.let {
                val user = authViewModel.uiState.value.user

                val owner = SessionOwner(
                    id = user?.id!!,
                    username = user.username,
                    zaddr = user.zaddr!!,
                    userId = user.id
                )

                val sessionWithOwner = it.toSessionWithOwner(owner)
                _uiState.update { s ->
                    s.copy(sessions = s.sessions + sessionWithOwner)
                }
            }
        } catch (e: Exception) {
            _uiState.update { it.copy(error = e.message) }
        }
    }

    fun joinSessionByInvite(code: String) = viewModelScope.launch {
        try {
            val session = sessionRepo.joinByInvite(code)
            session?.let { it ->
                val user = authViewModel.uiState.value.user

                val owner = SessionOwner(
                    id = user?.id!!,
                    username = user.username,
                    zaddr = user.zaddr!!,
                    userId = user.id
                )

                val sessionWithOwner = it.toSessionWithOwner(owner)
                _uiState.update { s -> s.copy(sessions = s.sessions + sessionWithOwner) }
            }
        } catch (e: Exception) {
            _uiState.update { it -> it.copy(error = e.message) }
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

    fun fetchSession(sessionId: String) = viewModelScope.launch {
        _uiState.update { it.copy(loading = true) }
        try {
            val response = sessionRepo.getSession(sessionId)
            response?.let { it ->

                _uiState.update {
                    it.copy(
                        loading = false,
                        sessions = it.sessions + response.session
                    )
                }

                participantViewModel.updateParticipants(sessionId, response.participants)
                expenseViewModel.updateExpenses(sessionId, response.expenses)
            }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(
                    loading = false,
                    error = e.message
                )
            }
            Log.e("AppStore", "fetchSession failed", e)
            null
        }
    }
}
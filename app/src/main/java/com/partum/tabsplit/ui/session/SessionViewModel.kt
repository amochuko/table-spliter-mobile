package com.partum.tabsplit.ui.session

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.partum.tabsplit.data.api.SessionRequest
import com.partum.tabsplit.data.model.Session
import com.partum.tabsplit.data.repository.SessionRepository
import com.partum.tabsplit.ui.auth.AuthViewModel
import com.partum.tabsplit.ui.expense.ExpenseViewModel
import com.partum.tabsplit.ui.participant.ParticipantViewModel
import com.partum.tabsplit.utils.combineDateAndTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.time.LocalTime
import java.util.Date

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
            val result = sessionRepo.getSessions()

            _uiState.update {
                it.copy(
                    ownedSessions = result.ownedSessions!!,
                    joinedSessions = result.joinedSessions!!,
                    loading = false,
                    error = null
                )
            }
        } catch (e: Exception) {
            val errorMsg = when (e) {
                is UnknownHostException,
                is SocketTimeoutException,
                is ConnectException -> "No internet"

                else -> e.localizedMessage ?: "An unexpected error occurred"
            }

            _uiState.update { it.copy(error = errorMsg, loading = false) }
        }
    }

    suspend fun createSession(
        title: String,
        description: String,
        startDate: Date,
        endDate: Date,
        startTime: LocalTime,
        endTime: LocalTime,
    ): Session? {

        val startDateTime = combineDateAndTime(startDate, startTime)
        val endDateTime = combineDateAndTime(endDate, endTime)

        return try {
            val session = sessionRepo.createSession(
                SessionRequest(
                    title,
                    description,
                    startDateTime,
                    endDateTime
                )
            )

            session?.let {
                _uiState.update { s ->
                    s.copy(sessions = s.sessions + it)
                }
            }

            session
        } catch (e: Exception) {
            Log.e("SessionViewModel::createSession", e.message.toString())

            _uiState.update { it.copy(error = e.message) }
            null
        }
    }

    fun joinSessionByInvite(code: String) = viewModelScope.launch {
        _uiState.update { it.copy(error = null, hasJoinedSession = false) }

        try {
            val joinedSession = sessionRepo.joinByInvite(code)

            if (joinedSession != null) {

                _uiState.update { s ->
                    s.copy(
                        sessions = s.sessions + joinedSession,
                        hasJoinedSession = true,
                        session = joinedSession,
                        error = null
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        error = "Invalid or expired invite code!"
                    )
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
            _uiState.update { it ->
                it.copy(
                    hasJoinedSession = false,
                    error = "Unable to join session: ${e.message}"
                )
            }
        }
    }

    fun deleteSession(sessionId: String) = viewModelScope.launch {
        _uiState.update { it.copy(loading = true) }

        try {
            sessionRepo.deleteSession(sessionId).let {
                _uiState.update {
                    it.copy(
                        loading = false,
                        isDeleted = it.isDeleted,
                        sessions = it.sessions.filterNot { s -> s.id == sessionId }
                    )
                }
            }

        } catch (e: Exception) {
            Log.e("SessionViewModel::deleteSession", e.message!!)
            _uiState.update {
                it.copy(
                    error = "Failed to delete of id: $sessionId",
                    loading = false,
                    isDeleted = false
                )
            }
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
                val swa = res.sessionWithExpensesAndParticipants!!

                _uiState.update {
                    it.copy(
                        loading = false,
                        session = swa.session,
                        sessions = (it.sessions + swa.session).distinctBy { s -> s.id },
                        sessionWithExpensesAndParticipants = swa
                    )
                }

                participantViewModel.updateParticipants(
                    sessionId, swa.participants
                )

                expenseViewModel.updateExpenses(
                    sessionId, swa.expenses
                )
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
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun leaveSession(sessionId: String) = viewModelScope.launch {
        _uiState.update {
            it.copy(loading = true)
        }

        try {
            sessionRepo.leaveSession(sessionId).let {
                Log.d("SessionViewModel::leaveSession", it!!)

                _uiState.update {
                    it.copy(
                        loading = false,
                        sessions = it.sessions.filterNot { s -> s.id == sessionId },
                    )
                }
            }
        } catch (e: Exception) {

            Log.d("SessionViewModel::leaveSession", e.message!!)
            _uiState.update {
                it.copy(
                    loading = false,
                    error = "Failed to leave session",
                )
            }
        }
    }
}
package com.partum.tabsplit.ui.session

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.partum.tabsplit.data.api.SessionRequest
import com.partum.tabsplit.data.model.Session
import com.partum.tabsplit.data.model.SessionWithExpensesAndParticipants
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
            val sessions = sessionRepo.getSessions()
//            val mapped = sessions.map { it.toFullSession() }

            _uiState.update {
                it.copy(
                    sessions = sessions,
                    loading = false
                )
            }
        } catch (e: Exception) {
            _uiState.update { it.copy(error = e.message, loading = false) }
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

        Log.d(
            "SessionViewModel::createSessionArgs",
            "$title, $description, " +
                    "$startDateTime, $endDateTime"
        )
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
        try {
            val joinedSession = sessionRepo.joinByInvite(code)

            joinedSession?.let { it ->
                _uiState.update { s ->
                    s.copy(
                        sessions = s.sessions + it,
                        joinedSession = true,
                        session = it
                    )
                }
            }
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
            Log.d("SessionViewModel::FetchSession", "$response")

            response?.let { res ->
                val (session, participants, expenses) = res.sessionWithExpensesAndParticipants

                val combined = SessionWithExpensesAndParticipants(
                    session,
                    participants,
                    expenses,
                )

                _uiState.update {
                    it.copy(
                        loading = false,
                        session = session,
                        sessions = (it.sessions + session).distinctBy { s -> s.id },
                        sessionWithExpensesAndParticipants = combined
                    )
                }

                participantViewModel.updateParticipants(
                    sessionId, participants
                )

                expenseViewModel.updateExpenses(
                    sessionId, expenses
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

}
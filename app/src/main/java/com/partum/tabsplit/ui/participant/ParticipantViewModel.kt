package com.partum.tabsplit.ui.participant

import androidx.lifecycle.ViewModel
import com.partum.tabsplit.data.api.SessionWithExpensesAndParticipantsResponse
import com.partum.tabsplit.data.model.Participant
import com.partum.tabsplit.data.repository.SessionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


class ParticipantViewModel(
    private val sessionRepo: SessionRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ParticipantUiState())
    val uiState: StateFlow<ParticipantUiState> = _uiState.asStateFlow()

    fun fetchParticipants(sessionId: String, session: SessionWithExpensesAndParticipantsResponse) {
        try {
            session.let {
                val updated = _uiState.value.participants.toMutableMap()
                updated[sessionId] = it.sessionWithExpensesAndParticipants!!.participants

                _uiState.update { state -> state.copy(participants = updated) }
            }
        } catch (e: Exception) {
            _uiState.update { it.copy(error = e.message) }
        }
    }

    fun updateParticipants(sessionId: String, participants: List<Participant>) {
        _uiState.update {
            it.copy(
                participants = it.participants + (sessionId to participants)
            )
        }
    }
}
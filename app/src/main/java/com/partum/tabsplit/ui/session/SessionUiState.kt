package com.partum.tabsplit.ui.session

import com.partum.tabsplit.data.model.Session
import com.partum.tabsplit.data.model.SessionWithExpensesAndParticipants

data class SessionUiState(
    val sessions: List<Session> = emptyList(),
    val sessionWithExpensesAndParticipants: SessionWithExpensesAndParticipants? = null,
    val session: Session? = null,
    val loading: Boolean = false,
    val error: String? = null,
    val pendingInviteCode: String? = null,
    val joinedSession: Boolean = false
)
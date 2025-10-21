package com.ochuko.tabsplit.ui.session

import com.ochuko.tabsplit.data.api.SessionWithOwner
import com.ochuko.tabsplit.data.model.Session

data class SessionUiState(
//    val sessions: List<Session> = emptyList(),
    val sessions: List<SessionWithOwner> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null,
    val pendingInviteCode: String? = null,
)
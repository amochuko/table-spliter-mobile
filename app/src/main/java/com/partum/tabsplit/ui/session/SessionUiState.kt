package com.ochuko.tabsplit.ui.session

import com.ochuko.tabsplit.data.model.FullSession

data class SessionUiState(
    val sessions: List<FullSession> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null,
    val pendingInviteCode: String? = null,
)
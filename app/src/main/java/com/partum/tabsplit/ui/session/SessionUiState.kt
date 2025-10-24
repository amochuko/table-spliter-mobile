package com.partum.tabsplit.ui.session

import com.partum.tabsplit.data.model.FullSession

data class SessionUiState(
    val sessions: List<FullSession> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null,
    val pendingInviteCode: String? = null,
)
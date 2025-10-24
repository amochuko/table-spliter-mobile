package com.partum.tabsplit.ui.participant

import com.partum.tabsplit.data.model.Participant

data class ParticipantUiState(
    val participants:Map<String, List<Participant>> = emptyMap(),
    val loading: Boolean = false,
    val error:String? = null
)
package com.ochuko.tabsplit.data.model

import com.ochuko.tabsplit.data.api.SessionOwner

// Combined model for UI layer
data class FullSession(
    val id: String,
    val title: String,
    val description: String,
    val currency: String,
    val createdAt: String,
    val createdBy: String,
    val owner: SessionOwner?,
    val inviteCode: String? = null,
    val inviteUrl: String? = null,
    val qrDataUrl: String? = null,
)

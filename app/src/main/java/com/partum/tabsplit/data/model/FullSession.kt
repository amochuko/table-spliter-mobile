package com.partum.tabsplit.data.model

import com.partum.tabsplit.data.api.SessionOwner
import java.util.Date

// Combined model for UI layer
data class FullSession(
    val id: String,
    val title: String,
    val description: String,
    val currency: String,
    val createdAt: Date,
    val createdBy: String?,
    val owner: SessionOwner?,
    val inviteCode: String? = null,
    val inviteUrl: String? = null,
    val qrDataUrl: String? = null,
    val startDateTime: Date,
    val endDateTime: Date
)

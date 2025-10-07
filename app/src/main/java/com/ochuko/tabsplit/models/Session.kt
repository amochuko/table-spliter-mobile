package com.ochuko.tabsplit.models

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Session(
    val id: String,
    val title: String,
    val description: String,
    val currency: String,
    val qrDatatUrl: String? = null,
    val inviteUrl: String? = null,

    @SerializedName("invite_code")
    val inviteCode: String? = null,

    @SerializedName("created_by")
    val createdBy: String,

    @SerializedName("created_at:")
    val createdAt: Date
)

package com.ochuko.tabsplit.data.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Session(
    val id: String,
    val title: String,
    val description: String,
    val currency: String,

    @SerializedName("invite_code")
    val inviteCode: String? = null,

    @SerializedName("qr_data_url")
    val qrDataUrl: String? = null,

    @SerializedName("invite_url")
    val inviteUrl: String? = null,

    @SerializedName("created_by")
    val createdBy: String,

    @SerializedName("created_at")
    val createdAt: Date
)

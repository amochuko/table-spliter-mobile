package com.ochuko.tabsplit.models

import com.google.gson.annotations.SerializedName

data class Session(
    val id: String,
    val title: String,
    val description: String,
    val currency: String,
    @SerializedName("invite_code")
    val inviteCode: String? = null,
    @SerializedName("created_by")
    val createdBy: String,
    @SerializedName("created_at:")
    val createdAt: String,
    val inviteUrl: String? = null
)

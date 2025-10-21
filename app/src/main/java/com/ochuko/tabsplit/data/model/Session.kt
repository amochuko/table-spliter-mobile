package com.ochuko.tabsplit.data.model

import com.google.gson.annotations.SerializedName
import com.ochuko.tabsplit.data.api.SessionOwner
import java.util.Date
import com.ochuko.tabsplit.data.api.SessionWithOwner

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


fun Session.toSessionWithOwner(owner: SessionOwner?=null):SessionWithOwner{
    return SessionWithOwner(
        id = id,
        title = title,
        description = description,
        currency = currency,
        createdAt = createdAt.toString(),
        owner = owner ?: SessionOwner(
            id = createdBy,
            username = "Unknown",
            zaddr = "",
            userId = createdBy
        )
    )
}
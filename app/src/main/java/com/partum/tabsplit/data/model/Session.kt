package com.partum.tabsplit.data.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Session(
    val id: String,
    val title: String,
    val description: String,
    val currency: String,
    val owner: User,

    @SerializedName("invite_code")
    val inviteCode: String? = null,

    @SerializedName("qr_data_url")
    val qrDataUrl: String? = null,

    @SerializedName("invite_url")
    val inviteUrl: String? = null,

    @SerializedName("created_by")
    val createdBy: String,

    @SerializedName("created_at")
    val createdAt: Date,

    @SerializedName("start_datetime")
    val startDateTime: Date,

    @SerializedName("end_datetime")
    val endDateTime: Date
)

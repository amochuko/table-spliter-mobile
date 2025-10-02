package com.ochuko.tabsplit.models

import com.google.gson.annotations.SerializedName

data class Session(
    val id: String,
    val title: String,
    val description: String,
    val currency: String,
    @SerializedName("invite_code")
    val inviteCode:String? = null,
    val owner: Participant,
    val inviteUrl:String? = null
)

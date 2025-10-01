package com.ochuko.tabsplit.models

data class Session(
    val id: String,
    val title: String,
    val description: String,
    val currency: String,
    val inviteCode:String? = null,
    val owner: Participant,
    val inviteUrl:String? = null
)

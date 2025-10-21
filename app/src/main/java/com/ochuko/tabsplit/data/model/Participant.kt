package com.ochuko.tabsplit.models

data class Participant(
    val id: String,
    val username: String,
    val userId:String? = null,
    val email:String? = null,
    val zaddr: String? = null
)

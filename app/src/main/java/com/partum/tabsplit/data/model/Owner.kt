package com.partum.tabsplit.data.model

import com.google.gson.annotations.SerializedName

data class Owner(
    val id: String,
    val username: String,
    val zaddr: String,
    val email: String?
)
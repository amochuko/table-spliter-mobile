package com.partum.tabsplit.data.model

import com.google.gson.annotations.SerializedName

data class Expense(
    val id: String,

    @SerializedName("session_id")
    val sessionId: String,

    val memo: String,
    val amount: Double,

    @SerializedName("payer_id")
    val payerId: String,

    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("payer_username")
    val payerUsername: String,

    @SerializedName("payer_participant_id")
    val payerParticipantId: String,
)

data class AddExpenseRequest(
    val memo: String,
    val amount: Double,
)



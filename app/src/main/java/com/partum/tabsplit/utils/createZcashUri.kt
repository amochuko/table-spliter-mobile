package com.partum.tabsplit.utils

import android.util.Log

fun createZcashUri(address: String, amount: Double, memo: String? = null): String {
    val isSapling = address.startsWith("zs") || address.startsWith("ztestsapling")
    val isUnified = address.startsWith("u1") || address.startsWith("utest")

    if (!isSapling && !isUnified) {
        throw IllegalArgumentException("Invalid Zcash address (must be Sapling or Unified).")
    }

    require(amount >= 0) { "Amount must be non-negative." }
    val roundedAmount = "%.8f".format(amount)

    val params = mutableListOf<String>()

    if (amount > 0) {
        params.add("amount=$roundedAmount")
    }

    memo?.let {
        val encodedMemo = java.net.URLEncoder.encode(it, "UTF-8")
            .replace("+", "%20")

        params.add("memo=$encodedMemo")
    }

    val query = if (params.isNotEmpty()) "?" + params.joinToString("&") else ""

    return "zcash:$address$query"
}
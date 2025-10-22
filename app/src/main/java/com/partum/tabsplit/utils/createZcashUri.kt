package com.ochuko.tabsplit.utils

fun createZcashUri(address: String,  amount: Double,  memo:String?=null):String {
    val isSapling = address.startsWith("zs") || address.startsWith("ztestsapling")
    val isUnified = address.startsWith("u1") || address.startsWith("utest")

    if (!isSapling && !isUnified) {
        throw IllegalArgumentException("Invalid Zcash address (must be Sapling or Unified).")
    }

    val params = mutableListOf<String>()

    if (amount > 0) {
        params.add("amount=${"%.8f".format(amount)}")
    }

    memo?.let {
        params.add("memo=${java.net.URLEncoder.encode(it, "UTF-8")}")
    }

    val query = if (params.isNotEmpty()) "?" + params.joinToString("&") else ""

    return "zcash:$address$query"
}
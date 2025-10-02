package com.ochuko.tabsplit.utils

fun shortString(id: String?): String {
    return if (id.isNullOrBlank()) {
        ""
    } else {
        "${id.take(6)}...${id.takeLast(6)}"
    }
}

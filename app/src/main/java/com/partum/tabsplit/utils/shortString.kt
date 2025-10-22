package com.partum.tabsplit.utils

fun shortString(addr: String, prefix: Int = 8, suffix: Int = 6): String {
    return if (addr.length > prefix + suffix) {
        "${addr.take(prefix)}...${addr.takeLast(suffix)}"
    } else addr
}

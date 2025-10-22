package com.ochuko.tabsplit.utils

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

fun parseIsoDate(dateString: String): Date? {
    return try {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        format.timeZone = TimeZone.getTimeZone("UTC")
        format.parse(dateString)
    } catch (e: Exception) {
        Log.e("parseIsoDate", "Date")
        null
    }
}

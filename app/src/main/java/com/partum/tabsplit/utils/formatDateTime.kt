package com.partum.tabsplit.utils

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

fun formatDate(dateTimeStr: String): String {
    return try {
        val zoned = parseDateTime(dateTimeStr)

        zoned.format(DateTimeFormatter.ofPattern("MMM dd, yyy", Locale.getDefault()))
    } catch (e: Exception) {
        ""
    }
}

fun formatTime(dateTimeStr: String): String {
    return try {
        val zoned = parseDateTime(dateTimeStr)

        zoned.format(DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault()))
    } catch (e: Exception) {
        ""
    }
}


private fun parseDateTime(dateTimeStr: String): ZonedDateTime {
    val possibleFormats = listOf(
        DateTimeFormatter.ISO_OFFSET_DATE_TIME,
        DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss 'GMT'XXX yyyy", Locale.ENGLISH),
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssXXX", Locale.ENGLISH)
    )

    for (fmt in possibleFormats) {
        try {
            return ZonedDateTime.parse(dateTimeStr, fmt)
        } catch (_: DateTimeParseException) {
        }
    }
    throw IllegalArgumentException("Unrecognized date format: $dateTimeStr")
}

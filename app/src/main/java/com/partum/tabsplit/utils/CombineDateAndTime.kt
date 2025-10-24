package com.partum.tabsplit.utils

import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Date

fun combineDateAndTime(date: Date,  time: LocalTime): String{
    if(time == null) return date.toInstant().toString()

    val zone = ZoneId.systemDefault()
    val localDate = date.toInstant().atZone(zone).toLocalDate()

    return ZonedDateTime.of(localDate, time, zone).toInstant().toString()

}
package com.daynest.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

fun LocalDate.toEpochDayLong(): Long = this.toEpochDay()

fun epochDayToLocalDate(epochDay: Long): LocalDate = LocalDate.ofEpochDay(epochDay)

fun LocalDate.friendlyLabel(): String {
    val today = LocalDate.now()
    return when (this) {
        today -> "Today"
        today.plusDays(1) -> "Tomorrow"
        today.minusDays(1) -> "Yesterday"
        else -> this.format(DateTimeFormatter.ofPattern("EEE, MMM d"))
    }
}

fun LocalDate.dayOfWeekShort(): String =
    this.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())

fun LocalDate.monthDay(): String = this.format(DateTimeFormatter.ofPattern("MMM d"))

package com.example.traindriver.ui.screen.adding_screen

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

fun <T> concatenate(vararg lists: List<T>): List<T> {
    return listOf(*lists).flatten()
}

@RequiresApi(Build.VERSION_CODES.O)
fun LocalDateTime.toLong(): Long {
    val zoneDateTime = ZonedDateTime.of(this, ZoneId.systemDefault())
    return zoneDateTime.toInstant().toEpochMilli()
}

@RequiresApi(Build.VERSION_CODES.O)
fun getLocalDateListBeforeTarget(targetDate: LocalDate): MutableList<LocalDate> {
    var startDate = LocalDate.parse("1980-01-01")
    val list = mutableListOf<LocalDate>()
    while (startDate != targetDate) {
        list.add(startDate)
        startDate = startDate.plusDays(1)
    }
    return list
}

@RequiresApi(Build.VERSION_CODES.O)
fun getLocaleDateListAfterTarget(targetDate: LocalDate): MutableList<LocalDate> {
    var startDate = LocalDate.of(targetDate.year, 12, 31)
    val list = mutableListOf<LocalDate>()
    while (startDate != targetDate) {
        list.add(startDate)
        startDate = startDate.minusDays(1)
    }
    return list
}
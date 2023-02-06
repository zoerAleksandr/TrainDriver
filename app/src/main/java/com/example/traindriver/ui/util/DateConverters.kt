package com.example.traindriver.ui.util

import java.util.*

fun currentTimeInLong() = Calendar.getInstance().timeInMillis

fun getDay(date: Long?): String {
    val day = Calendar.getInstance().apply {
        timeInMillis = date ?: currentTimeInLong()
    }.get(Calendar.DAY_OF_MONTH).toString()

    return if (day.length == 1) { "0$day" } else { day }
}

fun getMonth(date: Long?): String {
    val month = Calendar.getInstance().apply {
        timeInMillis = date ?: currentTimeInLong()
    }.get(Calendar.MONTH).toString()

    return if (month.length == 1) { "0$month" } else { month }
}


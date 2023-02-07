package com.example.traindriver.ui.util

import java.util.*

fun currentTimeInLong() = Calendar.getInstance().timeInMillis

fun getDay(date: Long?): String {
    val day = Calendar.getInstance().apply {
        timeInMillis = date ?: currentTimeInLong()
    }.get(Calendar.DAY_OF_MONTH).toString()

    return if (day.length == 1) {
        "0$day"
    } else {
        day
    }
}

fun getMonth(date: Long?): String {
    return Calendar.getInstance().apply {
        timeInMillis = date ?: currentTimeInLong()
    }.get(Calendar.MONTH).getMonth()
}

private fun Int.getMonth(): String {
    return when (this) {
        0 -> {
            Month.JANUARY
        }
        1 -> {
            Month.FEBRUARY
        }
        2 -> {
            Month.MARCH
        }
        3 -> {
            Month.APRIL
        }
        4 -> {
            Month.MAY
        }
        5 -> {
            Month.JUNE
        }
        6 -> {
            Month.JULY
        }
        7 -> {
            Month.AUGUST
        }
        8 -> {
            Month.SEPTEMBER
        }
        9 -> {
            Month.OCTOBER
        }
        10 -> {
            Month.NOVEMBER
        }
        11 -> {
            Month.DECEMBER
        }
        else -> {
            ""
        }
    }
}

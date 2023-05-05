package com.example.traindriver.ui.util.long_util

fun Long.getHour(): Int {
    val totalMinute = this / 60_000
    return (totalMinute / 60).toInt()
}

fun Long.getRemainingMinuteFromHour(): Int {
    val totalMinute = this / 60_000
    return (totalMinute.rem(60)).toInt()
}

fun Long?.getTimeInStringFormat(): String {
    return if (this == null) {
        ""
    } else {
        val hour = this.getHour()
        val hourText = if (hour < 10) {
            "0$hour"
        } else {
            hour.toString()
        }
        val minute = this.getRemainingMinuteFromHour()
        val minuteText = if (minute < 10) {
            "0$minute"
        } else {
            minute.toString()
        }
        "$hourText:$minuteText"
    }
}

fun Long?.compareWithNullable(other: Long?): Boolean {
    return if (this == null || other == null) true
    else this < other
}
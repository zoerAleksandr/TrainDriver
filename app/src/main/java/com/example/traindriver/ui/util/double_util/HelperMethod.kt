package com.example.traindriver.ui.util.double_util

import kotlin.math.pow
import kotlin.math.roundToInt

fun differenceBetweenDouble(value1: Double?, value2: Double?): Double? {
    val countAfterPoint1: Int = value1?.countCharsAfterDecimalPoint() ?: 0
    val countAfterPoint2: Int = value2?.countCharsAfterDecimalPoint() ?: 0
    val maxCount = if (countAfterPoint1 > countAfterPoint2) {
        countAfterPoint1
    } else {
        countAfterPoint2
    }
    val result = value2 - value1
    return result?.let {
        rounding(it, maxCount)
    }
}

fun reverseDifferenceBetweenDouble(value1: Double?, value2: Double?): Double? {
    val countAfterPoint1: Int = value1?.countCharsAfterDecimalPoint() ?: 0
    val countAfterPoint2: Int = value2?.countCharsAfterDecimalPoint() ?: 0
    val maxCount = if (countAfterPoint1 > countAfterPoint2) {
        countAfterPoint1
    } else {
        countAfterPoint2
    }
    val result = value1 - value2
    return result?.let {
        rounding(it, maxCount)
    }
}

fun rounding(value: Double?, count: Int): Double? {
    return value?.let {
        (it * 10.0.pow(count)).roundToInt() / 10.0.pow(count)
    }
}

package com.example.traindriver.ui.util.double_util

import java.math.BigDecimal

fun Double?.plusNullableValue(other: Double?): Double? {
    return if (this == null) {
        null
    } else {
        this + other
    }
}

fun Double.countCharsAfterDecimalPoint(): Int {
    return BigDecimal.valueOf(this).scale()
}

fun Double.str(): String {
    return if (this % 1.0 == 0.0) {
        this.toString().dropLast(2)
    } else {
        this.toString()
    }
}
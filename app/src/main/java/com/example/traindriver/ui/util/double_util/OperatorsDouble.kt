package com.example.traindriver.ui.util.double_util

    operator fun Double?.minus(other: Double?): Double? =
        if (this != null && other != null) {
            this - other
        } else {
            null
        }

    operator fun Double?.times(other: Double?): Double? =
        if (this != null && other != null) {
            this * other
        } else {
            null
        }

    operator fun Double.plus(other: Double?): Double {
        return if (other == null) {
            this
        } else {
            this + other
        }
    }
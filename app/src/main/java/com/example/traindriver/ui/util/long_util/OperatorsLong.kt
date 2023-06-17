package com.example.traindriver.ui.util.long_util

operator fun Long?.minus(other: Long?): Long? {
   return if (this != null && other != null) {
        this - other
    } else {
        null
    }
}

operator fun Long?.plus(other: Long): Long? {
   return if (this != null) {
        this + other
    } else {
        null
    }
}

operator fun Long?.plus(other: Long?): Long? {
   return if (this != null && other != null) {
        this + other
    } else {
        null
    }
}

operator fun Long?.div(other: Long): Long? {
   return if (this != null) {
        this / other
    } else {
        null
    }
}
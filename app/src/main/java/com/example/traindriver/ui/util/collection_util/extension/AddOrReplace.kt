package com.example.traindriver.ui.util.collection_util.extension

fun <T> MutableList<T>.addOrReplace(element: T) {
    val searchElement = this.find {
        it == element
    }

    if (searchElement == null) {
        this.add(element)
    } else {
        val index = this.indexOf(searchElement)
        this[index] = element
    }
}
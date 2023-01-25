package com.example.traindriver.data.util

sealed class ResultState<out T> {
    data class Success<out R>(val data:R?) : ResultState<R>()
    data class Failure(val msg:Throwable) : ResultState<Nothing>()
    data class Loading(val msg:String?) : ResultState<Nothing>()
}

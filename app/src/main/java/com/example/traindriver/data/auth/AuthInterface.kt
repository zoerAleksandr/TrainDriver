package com.example.traindriver.data.auth

import com.example.traindriver.data.util.ResultState
import kotlinx.coroutines.flow.Flow

interface AuthInterface {
    fun signIn(): Flow<ResultState<String>>
}
package com.example.traindriver.ui.util

import com.example.traindriver.data.auth.SignInMethod

interface LogInToApp {
    fun logIn(method: SignInMethod)
}
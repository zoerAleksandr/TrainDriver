package com.example.traindriver.domain.use_case

import com.example.traindriver.data.auth.SignInMethod

class SignInUseCase {
    suspend fun execute(method: SignInMethod) {
        method.signIn()
    }
}
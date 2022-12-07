package com.example.traindriver.domain.use_case

import com.example.traindriver.data.auth.SignInMethod

class SignInAnonymousUseCase {
    fun signIn() =
        SignInMethod.Anonymous.signIn()
}
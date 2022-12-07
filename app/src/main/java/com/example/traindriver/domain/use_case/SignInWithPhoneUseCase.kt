package com.example.traindriver.domain.use_case

import android.app.Activity
import com.example.traindriver.data.auth.PhoneAuth

class SignInWithPhoneUseCase {
    private val phoneAuth = PhoneAuth()
    fun createUserWithPhone(phone: String, activity: Activity) =
        phoneAuth.createUserWithPhone(phone, activity)

    fun verifyCode(code: String) =
        phoneAuth.verifyCode(code)

    fun resendCode(phone: String, activity: Activity) =
        phoneAuth.resendCode(phone, activity)
}
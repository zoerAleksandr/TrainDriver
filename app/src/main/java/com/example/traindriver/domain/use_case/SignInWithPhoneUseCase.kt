package com.example.traindriver.domain.use_case

import android.app.Activity
import com.example.traindriver.data.auth.AuthWithPhone

class SignInWithPhoneUseCase {
    private val authWithPhone = AuthWithPhone()
    fun createUserWithPhone(phone: String, activity: Activity) =
        authWithPhone.createUserWithPhone(phone, activity)

    fun verifyCode(code: String) =
        authWithPhone.verifyCode(code)

    fun resendCode(phone: String, activity: Activity) =
        authWithPhone.resendCode(phone, activity)
}
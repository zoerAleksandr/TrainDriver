package com.example.traindriver.domain.use_case

import android.app.Activity
import com.example.traindriver.data.auth.SignInMethod

class SignInUseCase {
    fun anonymousAuth() =
        SignInMethod.Anonymous.signIn()


    fun withPhone(phone: String, activity: Activity) =
        SignInMethod.Phone(phone, activity).signIn()

}
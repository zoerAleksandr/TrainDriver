package com.example.traindriver.domain.use_case

import androidx.lifecycle.LifecycleOwner
import com.example.traindriver.data.auth.AuthAnonymous
import org.koin.core.component.KoinComponent

class SignInAnonymousUseCase: KoinComponent {
    fun signIn(owner: LifecycleOwner) = AuthAnonymous.signIn(owner)
}
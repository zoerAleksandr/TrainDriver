package com.example.traindriver.domain.use_case

import com.example.traindriver.data.auth.AuthWithGoogle
import com.google.firebase.auth.AuthCredential
import org.koin.core.component.KoinComponent

class SignInWithGoogleUseCase : KoinComponent {
    private val authWithGoogle = AuthWithGoogle()

    fun oneTapSignInWithGoogle() = authWithGoogle.oneTap()

    fun signIn(googleCredential: AuthCredential) =
        authWithGoogle.signIn(googleCredential)
}
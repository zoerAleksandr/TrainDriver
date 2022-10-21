package com.example.traindriver.data.auth

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

sealed class SignInMethod : AuthInterface, KoinComponent {
    val callback: SignInCallback by inject()
    val auth = Firebase.auth

    object Anonymous : SignInMethod() {
        override suspend fun signIn() {
            // Вынести в Work Manager
            CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
                try {
                    auth.signInAnonymously()
                    callback.updateUI()
                } catch (e: Exception) {
                    Log.d("ZZZ", "catch ${e.message}")
                }
            }
        }
    }

    object Google : SignInMethod() {
        override suspend fun signIn() {
            TODO("Not yet implemented")
        }

    }

    object Email : SignInMethod() {
        override suspend fun signIn() {
            TODO("Not yet implemented")
        }

    }

    object Phone : SignInMethod() {
        override suspend fun signIn() {
            TODO("Not yet implemented")
        }
    }
}
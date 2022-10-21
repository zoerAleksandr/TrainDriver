package com.example.traindriver.data.auth

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class SignInFirebaseProvider(private val callback: SignInCallback) {
    private val auth = Firebase.auth

    fun checkSignIn() {
        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            if (auth.currentUser != null) {
                callback.updateUI()
            } else {
                Log.d("ZZZ", "currentUser = null")
            }
        }
    }

    // изменить на sealed class
    fun signIn(method: SignInMethod) {
        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            method.signIn()
        }
    }
}
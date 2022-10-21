package com.example.traindriver.di

import android.util.Log
import com.example.traindriver.data.auth.SignInCallback
import com.example.traindriver.data.auth.SignInFirebaseProvider
import org.koin.dsl.module

val signInModule = module {
    single<SignInCallback> {
        object : SignInCallback {
            override fun updateUI() {
                Log.d("ZZZ", "update UI from Koin")
            }
        }
    }
    single { SignInFirebaseProvider(callback = get()) }
}
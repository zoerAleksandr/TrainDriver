package com.example.traindriver.di

import com.example.traindriver.data.auth.SignInFirebaseProvider
import org.koin.dsl.module

val signInModule = module {
    single { SignInFirebaseProvider(callback = get()) }
}
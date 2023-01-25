package com.example.traindriver.di

import com.example.traindriver.domain.use_case.GetLocaleUseCase
import com.example.traindriver.domain.use_case.SignInAnonymousUseCase
import com.example.traindriver.domain.use_case.SignInWithGoogleUseCase
import com.example.traindriver.domain.use_case.SignInWithPhoneUseCase
import org.koin.dsl.module

val useCaseModule = module {
    single { GetLocaleUseCase(retrofitClient = get()) }
    single { SignInAnonymousUseCase() }
    single { SignInWithPhoneUseCase() }
    single { SignInWithGoogleUseCase() }
}
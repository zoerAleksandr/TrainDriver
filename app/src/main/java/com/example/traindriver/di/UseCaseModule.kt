package com.example.traindriver.di

import com.example.traindriver.domain.use_case.*
import org.koin.dsl.module

val useCaseModule = module {
    single { GetLocaleUseCase(retrofitClient = get()) }
    single { SignInAnonymousUseCase() }
    single { SignInWithPhoneUseCase() }
    single { SignInWithGoogleUseCase() }

    single { GetRouteListByMonthUseCase(repository = get()) }
    single { GetRouteByIdUseCase(repository = get()) }

    single { AddLocomotiveInRouteUseCase(repository = get()) }
}
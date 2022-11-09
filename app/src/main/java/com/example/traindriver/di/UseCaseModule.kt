package com.example.traindriver.di

import com.example.traindriver.domain.use_case.GetLocaleUseCase
import com.example.traindriver.domain.use_case.SaveLocaleInLocalStorageUseCase
import com.example.traindriver.domain.use_case.SignInUseCase
import org.koin.dsl.module

val useCaseModule = module {
    single { GetLocaleUseCase(retrofitClient = get()) }
    single { SignInUseCase() }
    single { SaveLocaleInLocalStorageUseCase() }
}
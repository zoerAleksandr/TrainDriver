package com.example.traindriver.di

import com.example.traindriver.domain.use_case.GetLocaleUseCase
import org.koin.dsl.module

val useCaseModule = module {
    single { GetLocaleUseCase(retrofitClient = get()) }
}
package com.example.traindriver.di

import com.example.traindriver.data.repository.DataStoreRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {
    single { DataStoreRepository(androidContext()) }
}
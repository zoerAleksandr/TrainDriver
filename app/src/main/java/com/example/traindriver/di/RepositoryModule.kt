package com.example.traindriver.di

import com.example.traindriver.data.repository.DataRepository
import com.example.traindriver.data.repository.DataStoreRepository
import com.example.traindriver.data.repository.mock.MockDataRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {
    single { DataStoreRepository(androidContext()) }
    single { androidContext().resources }

    single<DataRepository> { MockDataRepository() }
}
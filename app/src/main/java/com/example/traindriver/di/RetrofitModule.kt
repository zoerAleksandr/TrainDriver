package com.example.traindriver.di

import com.example.traindriver.data.retrofit.locale.LocationRetrofitClient
import org.koin.dsl.module

val retrofitModule = module {
    single { LocationRetrofitClient() }
}
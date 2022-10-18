package com.example.traindriver

import android.app.Application
import com.example.traindriver.di.retrofitModule
import com.example.traindriver.di.useCaseModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(
                retrofitModule,
                useCaseModule
            )
        }
    }
}
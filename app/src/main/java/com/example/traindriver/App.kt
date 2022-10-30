package com.example.traindriver

import android.app.Application
import androidx.work.Configuration
import androidx.work.DelegatingWorkerFactory
import com.example.traindriver.data.work_manager.WorkManagerFactory
import com.example.traindriver.di.*
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application(), Configuration.Provider {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(
                retrofitModule,
                useCaseModule,
                signInModule,
                workManagerModule,
                repositoryModule,
                viewModelModule
            )
        }
    }

    override fun getWorkManagerConfiguration(): Configuration {
        val workerFactory = DelegatingWorkerFactory()
        workerFactory.addFactory(WorkManagerFactory())

        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }
}
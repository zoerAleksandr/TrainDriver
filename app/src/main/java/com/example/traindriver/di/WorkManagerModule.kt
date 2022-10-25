package com.example.traindriver.di

import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.traindriver.R
import com.example.traindriver.data.worker.AnonymousSignInWorker
import com.example.traindriver.data.worker.ConstrainsWorkerEnum
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

val workManagerModule = module {
    single { WorkManager.getInstance(androidContext()) }
    single(named(ConstrainsWorkerEnum.NETWORK_CONNECTED)) {
        Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
    }
    single {
        OneTimeWorkRequestBuilder<AnonymousSignInWorker>()
            .setConstraints(get(named(ConstrainsWorkerEnum.NETWORK_CONNECTED)))
            .addTag(androidContext().resources.getString(R.string.ANONYMOUS_WORKER_TAG))
            .build()
    }
}
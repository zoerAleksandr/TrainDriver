package com.example.traindriver.di

import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.traindriver.data.worker.AnonymousSignInWorker
import com.example.traindriver.ui.util.Constants.ANONYMOUS_SIGN_IN_WORKER
import com.example.traindriver.ui.util.Constants.NETWORK_CONNECTED
import com.example.traindriver.ui.util.Tags.ANONYMOUS_WORKER_TAG
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

val workManagerModule = module {
    single { WorkManager.getInstance(androidContext()) }
    single(named(NETWORK_CONNECTED)) {
        Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
    }
    single(named(ANONYMOUS_SIGN_IN_WORKER)) {
        OneTimeWorkRequestBuilder<AnonymousSignInWorker>()
            .setConstraints(get(named(NETWORK_CONNECTED)))
            .addTag(ANONYMOUS_WORKER_TAG)
            .build()
    }
}
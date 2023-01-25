package com.example.traindriver.data.work_manager

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.example.traindriver.data.worker.AnonymousSignInWorker

class WorkManagerFactory : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            AnonymousSignInWorker::class.java.name ->
                AnonymousSignInWorker(appContext, workerParameters)
            else -> null
        }
    }
}
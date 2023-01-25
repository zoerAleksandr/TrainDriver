package com.example.traindriver.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.traindriver.data.repository.DataStoreRepository
import com.google.firebase.auth.FirebaseAuth
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AnonymousSignInWorker(
    ctx: Context,
    params: WorkerParameters
) : CoroutineWorker(ctx, params), KoinComponent {
    private val dataStore: DataStoreRepository by inject()
    private val auth: FirebaseAuth by inject()

    override suspend fun doWork(): Result {
        return try {
            auth.signInAnonymously().result.user?.let {
                dataStore.saveUid(it.uid)
            }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
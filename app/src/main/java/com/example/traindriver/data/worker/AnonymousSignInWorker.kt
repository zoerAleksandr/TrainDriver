package com.example.traindriver.data.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.traindriver.data.repository.DataStoreRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AnonymousSignInWorker(
    ctx: Context,
    params: WorkerParameters
) : CoroutineWorker(ctx, params), KoinComponent {
    private val dataStore: DataStoreRepository by inject()

    override suspend fun doWork(): Result {
        Log.d("ZZZ", "anonymous doWork start")
        return try {
            val auth = Firebase.auth
            val user = auth.signInAnonymously().result.user
            user?.let {
                Log.d("ZZZ", "uid = ${user.uid}")
                dataStore.saveUid(user.uid)
            }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
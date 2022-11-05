package com.example.traindriver.data.auth

import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.traindriver.data.worker.SignInWorkers
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

sealed class SignInMethod : AuthInterface, KoinComponent {
    protected val workManager: WorkManager by inject()

    object Anonymous : SignInMethod() {
        private val anonymousRequest: OneTimeWorkRequest by inject(
            named(SignInWorkers.ANONYMOUS_SIGN_IN_WORKER.name)
        )

        override suspend fun signIn() {
            workManager.enqueue(anonymousRequest)
        }
    }

    object Google : SignInMethod() {
        override suspend fun signIn() {
            TODO("Not yet implemented")
        }

    }

    object Email : SignInMethod() {
        override suspend fun signIn() {
            TODO("Not yet implemented")
        }

    }

    object Phone : SignInMethod() {
        override suspend fun signIn() {
            TODO()
        }
    }
}
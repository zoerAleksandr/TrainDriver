package com.example.traindriver.data.auth

import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.traindriver.data.util.ResultState
import com.example.traindriver.ui.util.Constants.ANONYMOUS_SIGN_IN_WORKER
import kotlinx.coroutines.flow.callbackFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

sealed class SignInMethod : KoinComponent {
    protected val workManager: WorkManager by inject()

    object Anonymous : SignInMethod() {
        private val anonymousRequest: OneTimeWorkRequest by inject(
            named(ANONYMOUS_SIGN_IN_WORKER)
        )

        fun signIn() = callbackFlow {
            trySend(ResultState.Loading("Выполняется анонимный вход"))
            workManager.runCatching {
                this.enqueue(anonymousRequest)
            }
                .onSuccess {
                    trySend(ResultState.Success("Anonymous signIn"))
                }
                .onFailure {
                    trySend(ResultState.Failure(it))
                }
        }
    }
}
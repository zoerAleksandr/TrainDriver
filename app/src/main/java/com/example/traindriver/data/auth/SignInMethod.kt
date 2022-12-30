package com.example.traindriver.data.auth

import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.traindriver.data.util.ResultState
import com.example.traindriver.data.worker.SignInWorkers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

sealed class SignInMethod : KoinComponent {
    protected val workManager: WorkManager by inject()

    object Anonymous : SignInMethod() {
        private val anonymousRequest: OneTimeWorkRequest by inject(
            named(SignInWorkers.ANONYMOUS_SIGN_IN_WORKER.name)
        )

        fun signIn() =
//            var flow =
//                flow<ResultState<String>> { ResultState.Loading("Выполняется анонимный вход") }
            callbackFlow {
                trySend(ResultState.Loading("Выполняется анонимный вход"))
                workManager.runCatching {
                    this.enqueue(anonymousRequest)
                }
                    .onSuccess {
                        trySend(ResultState.Success("Anonymous signIn"))
//                        flow = flow { ResultState.Success("Anonymous signIn") }
                    }
                    .onFailure {
                        trySend(ResultState.Failure(it))
//                        flow = flow { ResultState.Failure(it) }
                    }


            }
//            return flow
    }
}


object Google : SignInMethod() {
    fun signIn(): Flow<ResultState<String>> {
        TODO("Not yet implemented")
    }
}
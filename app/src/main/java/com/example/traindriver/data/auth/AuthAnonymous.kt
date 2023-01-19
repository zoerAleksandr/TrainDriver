package com.example.traindriver.data.auth

import androidx.lifecycle.LifecycleOwner
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.traindriver.data.util.ResultState
import com.example.traindriver.ui.screen.signin_screen.AnonymousSignInResponse
import com.example.traindriver.ui.util.Constants.ANONYMOUS_SIGN_IN_WORKER
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

object AuthAnonymous : KoinComponent {
    private val workManager: WorkManager by inject()
    private val anonymousRequest: OneTimeWorkRequest by inject(
        named(ANONYMOUS_SIGN_IN_WORKER)
    )

    fun signIn(owner: LifecycleOwner): Flow<AnonymousSignInResponse> = callbackFlow {
        trySend(ResultState.Loading("Выполняется анонимный вход. Ожидайте."))
        workManager.apply {
            enqueue(anonymousRequest)
            getWorkInfoByIdLiveData(anonymousRequest.id)
                .observe(owner) { info ->
                    when (info.state) {
                        WorkInfo.State.SUCCEEDED -> {
                            trySend(ResultState.Success(true))
                        }
                        WorkInfo.State.FAILED -> {
                            trySend(ResultState.Failure(Throwable(info.state.name)))
                        }
                        else -> {}
                    }
                }
        }
        awaitClose { close() }
    }
}
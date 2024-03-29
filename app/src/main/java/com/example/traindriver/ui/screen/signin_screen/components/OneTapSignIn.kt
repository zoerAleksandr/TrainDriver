package com.example.traindriver.ui.screen.signin_screen.components

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import com.example.traindriver.data.util.ResultState
import com.example.traindriver.ui.screen.signin_screen.OneTapSignInResponse
import com.example.traindriver.ui.util.SnackbarMessage
import com.google.android.gms.auth.api.identity.BeginSignInResult
import kotlinx.coroutines.launch

@Composable
fun OneTapSignIn(
    oneTapResponse: OneTapSignInResponse,
    snackbarHostState: SnackbarHostState,
    loadingState: MutableState<Boolean>,
    launch: (result: BeginSignInResult) -> Unit
) {
    val scope = rememberCoroutineScope()

    when (oneTapResponse) {
        is ResultState.Loading -> {
            loadingState.value = true
            LaunchedEffect(oneTapResponse) {
                scope.launch {
                    snackbarHostState.showSnackbar(SnackbarMessage.CONNECTING_TO_SERVER_MSG)
                }
            }
        }
        is ResultState.Success -> oneTapResponse.data?.let {
            loadingState.value = false
            LaunchedEffect(it) {
                launch(it)
            }
        }
        is ResultState.Failure -> LaunchedEffect(Unit) {
            loadingState.value = false
            scope.launch {
                snackbarHostState.showSnackbar(SnackbarMessage.ERROR_TRY_AGAIN_MSG)
            }
        }
    }
}
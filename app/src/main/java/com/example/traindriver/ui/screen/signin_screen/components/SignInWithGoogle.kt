package com.example.traindriver.ui.screen.signin_screen.components

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import com.example.traindriver.data.util.ResultState
import com.example.traindriver.ui.screen.signin_screen.SignInWithGoogleResponse
import com.example.traindriver.ui.util.SnackbarMessage.CONNECTING_TO_SERVER_MSG
import com.example.traindriver.ui.util.SnackbarMessage.ERROR_TRY_AGAIN_MSG
import kotlinx.coroutines.launch

@Composable
fun SignInWithGoogle(
    signInWithGoogleResponse: SignInWithGoogleResponse,
    snackbarHostState: SnackbarHostState,
    loadingState: MutableState<Boolean>,
    navigateToMainScreen: (signedIn: Boolean) -> Unit
) {
    val scope = rememberCoroutineScope()

    when (signInWithGoogleResponse) {
        is ResultState.Loading -> {
            loadingState.value = true
            LaunchedEffect(signInWithGoogleResponse) {
                scope.launch {
                    snackbarHostState.showSnackbar(CONNECTING_TO_SERVER_MSG)
                }
            }
        }
        is ResultState.Success -> signInWithGoogleResponse.data?.let { signedIn ->
            loadingState.value = false
            LaunchedEffect(signedIn) {
                navigateToMainScreen(signedIn)
            }
        }
        is ResultState.Failure -> {
            loadingState.value = false
            LaunchedEffect(signInWithGoogleResponse.msg) {
                scope.launch {
                    snackbarHostState.showSnackbar(ERROR_TRY_AGAIN_MSG)
                }
            }
        }
    }
}
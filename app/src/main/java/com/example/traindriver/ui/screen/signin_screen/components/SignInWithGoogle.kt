package com.example.traindriver.ui.screen.signin_screen.components

import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
    navigateToMainScreen: (signedIn: Boolean) -> Unit
) {
    val scope = rememberCoroutineScope()

    when (signInWithGoogleResponse) {
        is ResultState.Loading -> {
            scope.launch {
                snackbarHostState.showSnackbar(CONNECTING_TO_SERVER_MSG)
            }
        }
        is ResultState.Success -> signInWithGoogleResponse.data?.let { signedIn ->
            LaunchedEffect(signedIn) {
                navigateToMainScreen(signedIn)
            }
        }
        is ResultState.Failure -> {
            scope.launch {
                snackbarHostState.showSnackbar(ERROR_TRY_AGAIN_MSG)
            }
        }
    }
}
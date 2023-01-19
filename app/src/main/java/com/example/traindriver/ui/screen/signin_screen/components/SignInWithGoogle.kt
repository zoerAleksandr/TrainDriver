package com.example.traindriver.ui.screen.signin_screen.components

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.example.traindriver.data.util.ResultState
import com.example.traindriver.ui.screen.signin_screen.SignInWithGoogleResponse


@Composable
fun SignInWithGoogle(
    signInWithGoogleResponse: SignInWithGoogleResponse,
    navigateToMainScreen: (signedIn: Boolean) -> Unit
) {
    when (signInWithGoogleResponse) {
        is ResultState.Loading -> {
            Log.d("ZZZ", "SignInWithGoogle Loading")
//            TODO()
        }
        is ResultState.Success -> signInWithGoogleResponse.data?.let { signedIn ->
            LaunchedEffect(signedIn) {
                navigateToMainScreen(signedIn)
            }
        }
        is ResultState.Failure -> {
            Log.d("ZZZ", "${signInWithGoogleResponse.msg}")
//            TODO()
        }
    }
}
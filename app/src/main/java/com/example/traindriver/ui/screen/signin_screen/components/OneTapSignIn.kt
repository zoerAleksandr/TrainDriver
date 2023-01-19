package com.example.traindriver.ui.screen.signin_screen.components

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.example.traindriver.data.util.ResultState
import com.example.traindriver.ui.screen.signin_screen.OneTapSignInResponse
import com.google.android.gms.auth.api.identity.BeginSignInResult

@Composable
fun OneTapSignIn(
    oneTapResponse: OneTapSignInResponse,
    launch: (result: BeginSignInResult) -> Unit
) {
    when (oneTapResponse) {
        is ResultState.Loading -> {
            Log.d("ZZZ", "OneTapSignIn Loading")
//            TODO()
        }
        is ResultState.Success -> oneTapResponse.data?.let {
            LaunchedEffect(it) {
                launch(it)
            }
        }
        is ResultState.Failure -> LaunchedEffect(Unit) {
            Log.d("ZZZ", "${oneTapResponse.msg}")
//            TODO()
        }
    }
}
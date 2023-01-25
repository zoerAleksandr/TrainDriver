package com.example.traindriver.ui.screen.signin_screen.components

import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavController
import com.example.traindriver.data.util.ResultState
import com.example.traindriver.ui.screen.ScreenEnum
import com.example.traindriver.ui.screen.signin_screen.CreateUserWithPhoneResponse
import com.example.traindriver.ui.screen.signin_screen.WithPhoneResponse
import com.example.traindriver.ui.util.SnackbarMessage
import kotlinx.coroutines.launch

@Composable
fun CreateUserWithPhone(
    createUserWithPhone: CreateUserWithPhoneResponse,
    snackbarHostState: SnackbarHostState,
    navController: NavController,
    loadingState: MutableState<Boolean>
) {
    val scope = rememberCoroutineScope()

    when (createUserWithPhone) {
        is ResultState.Loading -> {
            loadingState.value = true
            scope.launch {
                snackbarHostState.showSnackbar(SnackbarMessage.CONNECTING_TO_SERVER_MSG)
            }
        }
        is ResultState.Success -> createUserWithPhone.data?.let { result ->
            loadingState.value = false
            LaunchedEffect(result) {
                when (result) {
                    is WithPhoneResponse.SmsSend -> {
                        navController.navigate(ScreenEnum.PASSWORD_CONFIRMATION.name)
                    }
                    is WithPhoneResponse.AutoSignIn -> {
                        navController.navigate(ScreenEnum.MAIN.name)
                    }
                }
            }
        }
        is ResultState.Failure -> {
            loadingState.value = false
            scope.launch {
                snackbarHostState.showSnackbar(SnackbarMessage.ERROR_TRY_AGAIN_MSG)
            }
        }
    }
}
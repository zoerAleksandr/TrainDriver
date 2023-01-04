package com.example.traindriver.ui.screen.signin_screen

import android.app.Activity
import android.os.CountDownTimer
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.traindriver.PreferencesApp
import com.example.traindriver.data.util.ResultState
import com.example.traindriver.domain.use_case.GetLocaleUseCase
import com.example.traindriver.domain.use_case.SignInAnonymousUseCase
import com.example.traindriver.domain.use_case.SignInWithGoogleUseCase
import com.example.traindriver.domain.use_case.SignInWithPhoneUseCase
import com.example.traindriver.ui.util.LocaleUser
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.AuthCredential
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

typealias OneTapSignInResponse = ResultState<BeginSignInResult>
typealias SignInWithGoogleResponse = ResultState<Boolean>

class SignInViewModel : ViewModel(), KoinComponent {
    private val getLocaleUseCase: GetLocaleUseCase by inject()
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase by inject()
    val oneTapClient: SignInClient by inject()

    var oneTapSignInResponse by mutableStateOf<OneTapSignInResponse>(ResultState.Success(null))
        private set
    var signInWithGoogleResponse by mutableStateOf<SignInWithGoogleResponse>(
        ResultState.Success(
            false
        )
    )
        private set

    val locale: MutableState<LocaleUser> = mutableStateOf(LocaleUser.OTHER)
    val number: MutableState<String> = mutableStateOf(LocaleUser.OTHER.prefix())
    val allowEntry: MutableState<Boolean> = mutableStateOf(true)
    val timer: MutableState<Long> = mutableStateOf(60)
    val resetButtonEnable: MutableState<Boolean> = mutableStateOf(false)

    fun oneTapSignIn() = viewModelScope.launch {
        oneTapSignInResponse = ResultState.Loading(null)
        signInWithGoogleUseCase.oneTapSignInWithGoogle().collect {
            oneTapSignInResponse = it
        }
    }

    fun signInWithGoogle(googleCredential: AuthCredential) = viewModelScope.launch {
        signInWithGoogleResponse = ResultState.Loading(null)
        signInWithGoogleUseCase.firebaseSignInWithGoogle(googleCredential).collect {
            signInWithGoogleResponse = it
        }
    }

    val phoneAuth = object : PhoneAuthInterface {
        val signInWithPhoneUseCase: SignInWithPhoneUseCase by inject()

        override fun createUserWithPhone(activity: Activity) =
            signInWithPhoneUseCase.createUserWithPhone(number.value, activity)

        override fun checkCode(code: String) =
            signInWithPhoneUseCase.verifyCode(code)

        override fun resendCode(activity: Activity) =
            signInWithPhoneUseCase.resendCode(number.value, activity)
    }

    val countDownTimer = object : CountDownTimer(60_000L, 1_000L) {
        override fun onTick(millisUntilFinished: Long) {
            timer.value = millisUntilFinished / 1000
        }

        override fun onFinish() {
            resetButtonEnable.value = true
        }
    }

    val anonymousAuth = object : AnonymousAuthInterface {
        val signInAnonymousUseCase: SignInAnonymousUseCase by inject()
        override fun signIn() {
            signInAnonymousUseCase.signIn()
        }
    }

    init {
        viewModelScope.launch {
            try {
                withTimeout(PreferencesApp.startLoading) {
                    getLocaleUseCase.execute()
                        .collect { localeState ->
                            locale.value = localeState
                            number.value = localeState.prefix()
                            allowEntry.value = localeState == LocaleUser.OTHER
                        }
                }
            } catch (e: TimeoutCancellationException) {
                this.cancel()
            } catch (e: Throwable) {
                Log.d("ZZZ", "$e")
                this.cancel()
            }
        }
    }
}

interface PhoneAuthInterface {
    fun createUserWithPhone(activity: Activity): Flow<ResultState<String?>>
    fun checkCode(code: String): Flow<ResultState<String?>>
    fun resendCode(activity: Activity): Flow<ResultState<String?>>
}

interface AnonymousAuthInterface {
    fun signIn()
}
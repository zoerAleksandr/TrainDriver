package com.example.traindriver.ui.screen.signin_screen

import android.app.Activity
import android.os.CountDownTimer
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.traindriver.PreferencesApp
import com.example.traindriver.data.util.ResultState
import com.example.traindriver.domain.use_case.GetLocaleUseCase
import com.example.traindriver.domain.use_case.SignInAnonymousUseCase
import com.example.traindriver.domain.use_case.SignInWithPhoneUseCase
import com.example.traindriver.ui.util.LocaleUser
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SignInViewModel : ViewModel(), KoinComponent {
    private val getLocaleUseCase: GetLocaleUseCase by inject()

    val locale: MutableState<LocaleUser> = mutableStateOf(LocaleUser.OTHER)
    val number: MutableState<String> = mutableStateOf(LocaleUser.OTHER.prefix())
    val allowEntry: MutableState<Boolean> = mutableStateOf(true)
    val timer: MutableState<Long> = mutableStateOf(60)
    val resetButtonEnable: MutableState<Boolean> = mutableStateOf(false)

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
    fun createUserWithPhone(activity: Activity): Flow<ResultState<String>>
    fun checkCode(code: String): Flow<ResultState<FirebaseUser?>>
    fun resendCode(activity: Activity): Flow<ResultState<String>>
}

interface AnonymousAuthInterface {
    fun signIn()
}
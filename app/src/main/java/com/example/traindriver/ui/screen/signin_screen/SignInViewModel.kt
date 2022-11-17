package com.example.traindriver.ui.screen.signin_screen

import android.app.Activity
import android.app.Application
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.traindriver.PreferencesApp
import com.example.traindriver.domain.use_case.GetLocaleUseCase
import com.example.traindriver.domain.use_case.SignInUseCase
import com.example.traindriver.ui.util.LocaleUser
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SignInViewModel(application: Application) : AndroidViewModel(application), KoinComponent {
    private val signInUseCase: SignInUseCase by inject()
    private val getLocaleUseCase: GetLocaleUseCase by inject()

    val locale: MutableState<LocaleUser> = mutableStateOf(LocaleUser.OTHER)
    val number: MutableState<String> = mutableStateOf(LocaleUser.OTHER.prefix())
    val allowEntry: MutableState<Boolean> = mutableStateOf(true)

    fun signInAnonymous() {
        viewModelScope.launch {
            signInUseCase.anonymousAuth()
        }
    }

    fun signInWithPhone(activity: Activity) = signInUseCase.withPhone(number.value, activity)

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
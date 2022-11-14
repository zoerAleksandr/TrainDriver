package com.example.traindriver.ui.screen.signin_screen

import android.app.Application
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.traindriver.PreferencesApp
import com.example.traindriver.data.auth.SignInMethod
import com.example.traindriver.domain.use_case.GetLocaleUseCase
import com.example.traindriver.domain.use_case.SignInUseCase
import com.example.traindriver.ui.util.LocaleState
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SignInViewModel(application: Application) : AndroidViewModel(application), KoinComponent {
    private val signInUseCase: SignInUseCase by inject()
    private val getLocaleUseCase: GetLocaleUseCase by inject()

    val locale: MutableState<LocaleState> = mutableStateOf(LocaleState.OTHER)
    val number: MutableState<String> = mutableStateOf(LocaleState.OTHER.prefix())
    val allowEntry: MutableState<Boolean> = mutableStateOf(true)

    fun signIn(method: SignInMethod) {
        viewModelScope.launch {
            signInUseCase.execute(method)
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
                            allowEntry.value = localeState == LocaleState.OTHER
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
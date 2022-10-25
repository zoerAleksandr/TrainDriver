package com.example.traindriver.ui.signin_screen

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.traindriver.data.auth.SignInMethod
import com.example.traindriver.domain.use_case.GetLocaleUseCase
import com.example.traindriver.domain.use_case.SignInUseCase
import com.example.traindriver.ui.util.LocaleState
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SignInViewModel(application: Application) : AndroidViewModel(application), KoinComponent {
    private val getLocaleUseCase: GetLocaleUseCase by inject()
    private val signInUseCase: SignInUseCase by inject()

    private val _localeState: MutableLiveData<LocaleState> = MutableLiveData()
    val localeState: LiveData<LocaleState> = _localeState
    private val localeStateList = LocaleState.values().map { it.name }

    fun getLocale() {
        viewModelScope.launch {
            kotlin.runCatching {
                getLocaleUseCase.execute().countryCode
            }
                .onSuccess {
                    if (localeStateList.contains(it)) {
                        _localeState.postValue(LocaleState.valueOf(it))
                    } else {
                        _localeState.postValue(LocaleState.OTHER)
                    }
                }
                .onFailure {
                    Log.e("ZZZ", "${it.message}")
                }
        }
    }

    fun signIn(method: SignInMethod) {
        viewModelScope.launch {
            signInUseCase.execute(method)
        }
    }
}
package com.example.traindriver.ui.splash_screen

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.traindriver.domain.use_case.GetLocaleUseCase
import com.example.traindriver.ui.ScreenEnum
import com.example.traindriver.ui.util.LocaleState
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SplashViewModel : ViewModel(), KoinComponent {
    private val getLocaleUseCase: GetLocaleUseCase by inject()

    private val _isLoading: MutableState<Boolean> = mutableStateOf(true)
    val isLoading: State<Boolean> = _isLoading

    private val _startDestination: MutableState<String> = mutableStateOf(ScreenEnum.SIGN_IN.name)
    val startDestination: State<String> = _startDestination

    private val _locale: MutableState<LocaleState> = mutableStateOf(LocaleState.OTHER)
    val locale: State<LocaleState> = _locale

    init {
        viewModelScope.launch {
            getLocaleUseCase.execute().collect {
                _locale.value = it
            }
            // startDestination
            _isLoading.value = false
        }
    }
}
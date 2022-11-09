package com.example.traindriver.ui.screen.splash_screen

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.traindriver.PreferencesApp
import com.example.traindriver.data.repository.DataStoreRepository
import com.example.traindriver.domain.use_case.GetLocaleUseCase
import com.example.traindriver.domain.use_case.SaveLocaleInLocalStorageUseCase
import com.example.traindriver.ui.screen.ScreenEnum
import com.example.traindriver.ui.util.LocaleState
import kotlinx.coroutines.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SplashViewModel : ViewModel(), KoinComponent {
    private val getLocaleUseCase: GetLocaleUseCase by inject()
    private val repository: DataStoreRepository by inject()
    private val saveLocaleUseCase: SaveLocaleInLocalStorageUseCase by inject()

    private val _isLoading: MutableState<Boolean> = mutableStateOf(true)
    val isLoading: State<Boolean> = _isLoading

    private val _startDestination: MutableState<String> = mutableStateOf(ScreenEnum.SIGN_IN.name)
    val startDestination: State<String> = _startDestination

    val locale: MutableState<LocaleState> = mutableStateOf(LocaleState.OTHER)

    val number: MutableState<String> = mutableStateOf(LocaleState.OTHER.prefix())
    val allowEntry: MutableState<Boolean> = mutableStateOf(true)

    init {
        viewModelScope.launch {
            try {
                withTimeout(PreferencesApp.startLoading) {
                    initSetting()
                }
            } catch (e: TimeoutCancellationException) {
                _isLoading.value = false
            } catch (e: Throwable) {
                Log.d("ZZZ", "$e")
                _isLoading.value = false
            }
        }
    }

    private suspend fun initSetting() = coroutineScope {
        val isRegistered = async { getRegisteredState() }.run {
            this.await()
        }
        if (!isRegistered) {
            launch { getLocale() }.join()
            launch { saveLocaleUseCase.execute(locale.value) }.join()
        }
        _isLoading.value = false
    }


    private suspend fun getRegisteredState(): Boolean {
        val isRegistered = repository.readIsRegisteredState()
        return if (isRegistered) {
            _startDestination.value = ScreenEnum.MAIN.name
            true
        } else {
            _startDestination.value = ScreenEnum.SIGN_IN.name
            false
        }
    }

    private suspend fun getLocale() = coroutineScope {
        getLocaleUseCase.execute()
            .collect { localeState ->
                locale.value = localeState
                number.value = localeState.prefix()
                allowEntry.value = localeState == LocaleState.OTHER
            }
    }
}
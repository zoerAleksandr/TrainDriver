package com.example.traindriver.ui.screen.splash_screen

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.traindriver.PreferencesApp
import com.example.traindriver.data.repository.DataStoreRepository
import com.example.traindriver.ui.screen.Screen
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SplashViewModel : ViewModel(), KoinComponent {
    private val repository: DataStoreRepository by inject()

    private val _isLoading: MutableState<Boolean> = mutableStateOf(true)
    val isLoading: State<Boolean> = _isLoading

    private val _startDestination: MutableState<String> = mutableStateOf(Screen.SignIn.route)
    val startDestination: State<String> = _startDestination

    init {
        viewModelScope.launch {
            initSetting()
        }
    }

    private suspend fun initSetting() = coroutineScope {
        launch {
            getRegisteredState()
        }
        launch {
            delay(PreferencesApp.startLoading)
            _isLoading.value = false
        }
    }


    private suspend fun getRegisteredState(): Boolean {
        val isRegistered = repository.readIsRegisteredState()
        return if (isRegistered) {
            _startDestination.value = Screen.Home.route
            true
        } else {
            _startDestination.value = Screen.SignIn.route
            false
        }
    }
}
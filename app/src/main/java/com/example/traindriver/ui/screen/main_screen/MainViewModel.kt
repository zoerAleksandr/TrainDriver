package com.example.traindriver.ui.screen.main_screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.traindriver.data.repository.DataStoreRepository
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MainViewModel : ViewModel(), KoinComponent {
    private val dataStore: DataStoreRepository by inject()

    var uid by mutableStateOf("")
        private set

    init {
        viewModelScope.launch {
            dataStore.readUid().collect {
                uid = it ?: "Выполняется анонимная авторизация, подождите"
            }
        }
    }
}
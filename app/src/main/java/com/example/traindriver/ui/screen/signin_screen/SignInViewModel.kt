package com.example.traindriver.ui.screen.signin_screen

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.traindriver.data.auth.SignInMethod
import com.example.traindriver.domain.use_case.SignInUseCase
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SignInViewModel(application: Application) : AndroidViewModel(application), KoinComponent {
    private val signInUseCase: SignInUseCase by inject()

    fun signIn(method: SignInMethod) {
        viewModelScope.launch {
            signInUseCase.execute(method)
        }
    }
}
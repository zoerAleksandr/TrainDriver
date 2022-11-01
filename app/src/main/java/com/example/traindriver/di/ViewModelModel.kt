package com.example.traindriver.di

import com.example.traindriver.ui.screen.splash_screen.SplashViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { SplashViewModel() }
}
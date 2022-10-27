package com.example.traindriver.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.Scaffold
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.example.traindriver.ui.splash_screen.SplashViewModel
import com.example.traindriver.ui.theme.TrainDriverTheme

class StartActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel: SplashViewModel by viewModels()

        installSplashScreen().setKeepOnScreenCondition {
            viewModel.isLoading.value
        }

        setContent {
            TrainDriverTheme {
                Scaffold {
                    val locale by viewModel.locale
                    val screen by viewModel.startDestination
                    val navController = rememberNavController()
                    SetupNavGraph(
                        navController = navController,
                        startDestination = screen,
                        locale = locale
                    )
                }
            }
        }
    }
}
package com.example.traindriver.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.example.traindriver.ui.screen.SetupNavGraph
import com.example.traindriver.ui.screen.splash_screen.SplashViewModel
import com.example.traindriver.ui.theme.TrainDriverTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class StartActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel: SplashViewModel by viewModel()
        val locale = viewModel.locale
        val screen by viewModel.startDestination

        installSplashScreen().setKeepOnScreenCondition {
            viewModel.isLoading.value
        }

        setContent {
            TrainDriverTheme {
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
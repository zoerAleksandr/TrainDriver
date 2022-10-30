package com.example.traindriver.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.example.traindriver.ui.splash_screen.SplashViewModel
import com.example.traindriver.ui.theme.TrainDriverTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class StartActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: SplashViewModel by viewModel()
        installSplashScreen().setKeepOnScreenCondition {
            viewModel.isLoading.value
        }
        val locale by viewModel.locale
        val screen by viewModel.startDestination

        setContent {
            TrainDriverTheme {
                val navController = rememberNavController()
                Log.d("ZZZ", "main $locale")
                Log.d("ZZZ", "main $screen")
                SetupNavGraph(
                    navController = navController,
                    startDestination = screen,
                    locale = locale
                )
            }
        }
    }
}
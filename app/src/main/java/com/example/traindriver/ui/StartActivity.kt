package com.example.traindriver.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Scaffold
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.traindriver.ui.main_screen.MainScreen
import com.example.traindriver.ui.signin_screen.SignInScreen
import com.example.traindriver.ui.splash_screen.SplashScreen
import com.example.traindriver.ui.theme.TrainDriverTheme

class StartActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TrainDriverTheme {
                Scaffold {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = ScreenEnum.SPLASH.name
                    ) {
                        composable(ScreenEnum.SPLASH.name) { SplashScreen(navController) }
                        composable(ScreenEnum.SIGN_IN.name) { SignInScreen(navController) }
                        composable(ScreenEnum.MAIN.name) {
                            MainScreen(navController)
                        }
                    }
                }
            }
        }
    }
}
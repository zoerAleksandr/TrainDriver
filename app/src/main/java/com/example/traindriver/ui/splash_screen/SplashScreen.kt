package com.example.traindriver.ui.splash_screen

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.traindriver.ui.ScreenEnum


@Composable
fun SplashScreen(navController: NavController) {
    Column {
        Text(text = "Splash screen")
        Button(
            onClick = {
                navController
                    .navigate(route = ScreenEnum.SIGN_IN.name) {
                        popUpTo(ScreenEnum.SPLASH.name) { inclusive = true }
                    }
            }) {}
    }
}
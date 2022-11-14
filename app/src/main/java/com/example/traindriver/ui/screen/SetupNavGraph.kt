package com.example.traindriver.ui.screen

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.traindriver.ui.screen.main_screen.MainScreen
import com.example.traindriver.ui.screen.signin_screen.SignInScreen

@Composable
fun SetupNavGraph(
    navController: NavHostController,
    startDestination: String,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(ScreenEnum.SIGN_IN.name) { SignInScreen(navController) }
        composable(ScreenEnum.MAIN.name) {
            MainScreen(navController)
        }
    }
}
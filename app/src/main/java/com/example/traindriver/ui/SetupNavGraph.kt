package com.example.traindriver.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.traindriver.ui.main_screen.MainScreen
import com.example.traindriver.ui.signin_screen.SignInScreen
import com.example.traindriver.ui.util.LocaleState

@Composable
fun SetupNavGraph(
    navController: NavHostController,
    startDestination: String,
    locale: LocaleState
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
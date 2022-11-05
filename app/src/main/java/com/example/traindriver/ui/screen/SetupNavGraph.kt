package com.example.traindriver.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.traindriver.ui.screen.main_screen.MainScreen
import com.example.traindriver.ui.screen.signin_screen.SignInScreen
import com.example.traindriver.ui.util.LocaleState

@Composable
fun SetupNavGraph(
    navController: NavHostController,
    startDestination: String,
    locale: MutableState<LocaleState>
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(ScreenEnum.SIGN_IN.name) { SignInScreen(navController, locale) }
        composable(ScreenEnum.MAIN.name) {
            MainScreen(navController)
        }
    }
}
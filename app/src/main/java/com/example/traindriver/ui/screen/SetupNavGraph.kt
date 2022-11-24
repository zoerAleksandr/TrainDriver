package com.example.traindriver.ui.screen

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.traindriver.ui.screen.main_screen.MainScreen
import com.example.traindriver.ui.screen.password_conf_screen.PasswordConfScreen
import com.example.traindriver.ui.screen.signin_screen.SignInScreen

@Composable
fun SetupNavGraph(
    navController: NavHostController,
    startDestination: String,
    activity: Activity
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(ScreenEnum.SIGN_IN.name) { SignInScreen(navController, activity) }
        composable(ScreenEnum.MAIN.name) {
            MainScreen(navController)
        }
        composable(ScreenEnum.PASSWORD_CONFIRMATION.name) { PasswordConfScreen(navController)}
    }
}
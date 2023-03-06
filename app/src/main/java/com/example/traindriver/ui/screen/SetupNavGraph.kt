package com.example.traindriver.ui.screen

import android.app.Activity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.traindriver.ui.screen.adding_screen.AddingScreen
import com.example.traindriver.ui.screen.main_screen.MainScreen
import com.example.traindriver.ui.screen.password_conf_screen.PasswordConfScreen
import com.example.traindriver.ui.screen.setting_screen.SettingScreen
import com.example.traindriver.ui.screen.signin_screen.SignInScreen
import com.example.traindriver.ui.screen.signin_screen.SignInViewModel
import com.example.traindriver.ui.screen.viewing_route_screen.ViewingRouteScreen
import com.example.traindriver.ui.util.Constants.ROUTE
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SetupNavGraph(
    startDestination: String,
    activity: Activity
) {
    val signInViewModel: SignInViewModel = viewModel()
    val navController = rememberAnimatedNavController()

    AnimatedNavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.SignIn.route) {
            SignInScreen(navController, activity, signInViewModel)
        }
        composable(Screen.Home.route) {
            MainScreen(navController)
        }
        composable(Screen.PasswordConfirmation.route) {
            PasswordConfScreen(navController, signInViewModel, activity)
        }
        composable(
            route = Screen.ViewingRoute.route,
            arguments = listOf(navArgument(ROUTE){
                type = NavType.StringType
            })
        ) {
            ViewingRouteScreen(navController)
        }
        composable(Screen.Setting.route) {
            SettingScreen()
        }
        composable(Screen.Adding.route){
            AddingScreen(navController = navController)
        }
    }
}
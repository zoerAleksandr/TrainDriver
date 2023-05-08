package com.example.traindriver.ui.screen

import android.app.Activity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.traindriver.ui.screen.adding_screen.AddingScreen
import com.example.traindriver.ui.screen.adding_screen.AddingViewModel
import com.example.traindriver.ui.screen.adding_screen.adding_train.AddingTrainScreen
import com.example.traindriver.ui.screen.adding_screen.adding_loco.AddingLocoScreen
import com.example.traindriver.ui.screen.adding_screen.adding_passenger.AddingPassengerScreen
import com.example.traindriver.ui.screen.main_screen.HomeScreen
import com.example.traindriver.ui.screen.password_conf_screen.PasswordConfScreen
import com.example.traindriver.ui.screen.setting_screen.SettingScreen
import com.example.traindriver.ui.screen.signin_screen.SignInScreen
import com.example.traindriver.ui.screen.signin_screen.SignInViewModel
import com.example.traindriver.ui.screen.viewing_route_screen.ViewingRouteScreen
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
    val addingRouteViewModel: AddingViewModel = viewModel()
    val navController = rememberAnimatedNavController()

    AnimatedNavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.SignIn.route) {
            SignInScreen(navController, activity, signInViewModel)
        }
        composable(Screen.Home.route) {
            HomeScreen(navController)
        }
        composable(Screen.PasswordConfirmation.route) {
            PasswordConfScreen(navController, signInViewModel, activity)
        }
        composable(
            route = Screen.ViewingRoute.route,
            arguments = listOf(navArgument(ROUTE) {
                type = NavType.StringType
            })
        ) {
            ViewingRouteScreen(navController)
        }
        composable(Screen.Setting.route) {
            SettingScreen()
        }
        composable(
            route = Screen.Adding.route,
            arguments = listOf(navArgument(ROUTE) {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString(ROUTE)
            /**
             * backStackEntry.arguments?.getString(ROUTE) при отсутствии аргумента невозвращает null
             * а возвращает строковае значение константы ROUTE. Поэтому для отслеживания вызова
             * метода без аргумента в AddingScreen проверяю не на null а .contains(ROUTE)*/
            AddingScreen(
                navController = navController,
                uid = id ?: ROUTE,
                viewModel = addingRouteViewModel
            )
        }
        composable(
            route = Screen.AddingLoco.route,
            arguments = listOf(
                navArgument(LOCO_ID) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString(LOCO_ID)
            AddingLocoScreen(
                navController = navController,
                locoId = id,
                addingRouteViewModel = addingRouteViewModel
            )
        }
        composable(
            route = Screen.AddingTrain.route,
            arguments = listOf(
                navArgument(TRAIN_ID) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString(TRAIN_ID)
            AddingTrainScreen(
                navController = navController,
                trainId = id,
                addingRouteViewModel = addingRouteViewModel
            )
        }
        composable(
            route = Screen.AddingPassenger.route,
            arguments = listOf(
                navArgument(PASSENGER_ID) {
                    type = NavType.StringType
                }
            )
        ) {backStackEntry ->
            val id = backStackEntry.arguments?.getString(PASSENGER_ID)
            AddingPassengerScreen(
                navController = navController,
                passengerId = id,
                addingRouteViewModel = addingRouteViewModel
            )
        }
    }
}
package com.example.traindriver.ui.screen.signin_screen

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.traindriver.ui.theme.TrainDriverTheme
import com.example.traindriver.ui.util.DarkLightPreviews
import com.example.traindriver.ui.util.FontScalePreviews

@Composable
fun SignInScreen(
    navController: NavController,
) {
    Background()
    StartElements(navController = navController)
//    LoadingElement()
}

@Composable
@DarkLightPreviews
@FontScalePreviews
fun DefaultPreview() {
    TrainDriverTheme {
        Background()
//        StartElements(localeState = mutableStateOf(LocaleState.RU))
        //    LoadingElement()
    }
}
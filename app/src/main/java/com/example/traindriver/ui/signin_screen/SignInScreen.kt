package com.example.traindriver.ui.signin_screen

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.traindriver.ui.theme.TrainDriverTheme
import com.example.traindriver.ui.util.DarkLightPreviews
import com.example.traindriver.ui.util.FontScalePreviews
import com.example.traindriver.ui.util.LocaleState

@Composable
fun SignInScreen(
    navController: NavController,
    locale: LocaleState
) {
    Background()
    StartElements(localeState = locale)
//    LoadingElement()
}

@Composable
@DarkLightPreviews
@FontScalePreviews
fun DefaultPreview() {
    TrainDriverTheme {
        Background()
        StartElements(localeState = LocaleState.RU)
        //    LoadingElement()
    }
}
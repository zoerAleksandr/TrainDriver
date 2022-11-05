package com.example.traindriver.ui.screen.signin_screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.navigation.NavController
import com.example.traindriver.ui.theme.TrainDriverTheme
import com.example.traindriver.ui.util.DarkLightPreviews
import com.example.traindriver.ui.util.FontScalePreviews
import com.example.traindriver.ui.util.LocaleState

@Composable
fun SignInScreen(
    navController: NavController,
    locale: MutableState<LocaleState>
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
        StartElements(localeState = mutableStateOf(LocaleState.RU))
        //    LoadingElement()
    }
}
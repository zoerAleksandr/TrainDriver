package com.example.traindriver.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val DarkColorPalette = darkColors(
    primary = Gray700,
    primaryVariant = Gray900,
    secondary = Red300,
    secondaryVariant = Red700,
    background = Gray600,
    error = Red800,
    surface = Gray600,
    onPrimary = Gray200,
    onSurface = Gray700,
    onBackground = Gray100,
    onSecondary = Gray100
)

private val LightColorPalette = lightColors(
    primary = Gray500,
    primaryVariant = Gray700,
    secondary = Red300,
    secondaryVariant = Red700,
    background = Gray200,
    surface = Gray50,
    error = Red800,
    onPrimary = Gray900,
    onSurface = Gray500,
    onBackground = Gray700,
    onSecondary = Gray100
)

var BackgroundIcon: Color = BackgroundIconLight

@Composable
fun TrainDriverTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors: Colors
    if (darkTheme){
        colors = DarkColorPalette
        BackgroundIcon = BackgroundIconDark
    } else {
        colors = LightColorPalette
        BackgroundIcon = BackgroundIconLight
    }

    val systemUiController = rememberSystemUiController()
    systemUiController.setNavigationBarColor(color = colors.background)
    systemUiController.setStatusBarColor(color = colors.primaryVariant)

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = ShapeBackground,
        content = content
    )
}
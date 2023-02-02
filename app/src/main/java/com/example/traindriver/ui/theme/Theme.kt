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
    primary = Color(0xFFDADADA),
    primaryVariant = Color(0xFFB9B9B9),
    secondary = Color(0xFFC5C5C5),
    onSecondary = Color(0xFFFAFAFA),
    surface = Color(0xFF868686),
    onSurface = Color(0xFF656565),
    background = Color(0xFF5F5F5F),
    onBackground = Color(0xFFC5C5C5),
)

private val LightColorPalette = lightColors(
    primary = Color(0xFF434343),
    primaryVariant = Color(0xFFA8A8A8),
    secondary = Color(0xFF9E9E9E),
    onSecondary = Color(0xFFFAFAFA),
    surface = Color(0xFFDCDCDC),
    onSurface = Color(0xFFC5C5C5),
    background = Color(0xFFFAFAFA),
    onBackground = Color(0xFF5F5F5F),
)

var BackgroundIcon: Color = BackgroundIconLight
val SpecialColor = Color(0xFFD32F2F)
val SpecialDisableColor = Color(0xFFFF7979)
var BackgroundFirst = Color(0xFF9E9E9E)
var BackgroundSecond = Color(0xFFEEEEEE)

@Composable
fun TrainDriverTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors: Colors
    if (darkTheme) {
        colors = DarkColorPalette
        BackgroundIcon = BackgroundIconDark
        BackgroundFirst = Color(0xFF242424)
        BackgroundSecond = Color(0xFF3D3D3D)
    } else {
        colors = LightColorPalette
        BackgroundIcon = BackgroundIconLight
        BackgroundFirst = Color(0xFF9E9E9E)
        BackgroundSecond = Color(0xFFEEEEEE)
    }

    val systemUiController = rememberSystemUiController()
    val useDarkIcons = !isSystemInDarkTheme()

    systemUiController.setStatusBarColor(
        color = Color.Transparent,
        darkIcons = useDarkIcons,
    )
    systemUiController.setNavigationBarColor(
        color = Color.Transparent,
        darkIcons = useDarkIcons,
        navigationBarContrastEnforced = false
    )

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = ShapeBackground,
        content = content
    )
}
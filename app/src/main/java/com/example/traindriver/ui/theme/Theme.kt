package com.example.traindriver.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val DarkColorPalette = darkColors(
    primary = Color(0xFFDADADA),
    primaryVariant = Color(0xFFB9B9B9),
    secondary = Color(0xFFC5C5C5),
    secondaryVariant = Color(0xFF86B0F3),
    onSecondary = Color(0xFFFAFAFA),
    surface = Color(0xFFB9B9B9),
    onSurface = Color(0xFF383838),
    background = Color(0xFF5F5F5F),
    onBackground = Color(0xFFC5C5C5),
)

private val LightColorPalette = lightColors(
    primary = Color(0xFF434343),
    primaryVariant = Color(0xFFA8A8A8),
    secondary = Color(0xFF9E9E9E),
    secondaryVariant = Color(0xFF1971E3),
    onSecondary = Color(0xFFFAFAFA),
    surface = Color(0xFFECECEC),
    onSurface = Color(0xFF979797),
    background = Color(0xFFFAFAFA),
    onBackground = Color(0xFF5F5F5F),
)

var BackgroundIcon: Color = BackgroundIconLight
val SpecialColor = Color(0xFFAD2727)
val SpecialDisableColor = Color(0xFFFF7979)
var BackgroundFirst = Color(0xFF9E9E9E)
var BackgroundSecond = Color(0xFFEEEEEE)
var ColorClickableText = Color(0xFF0000FF)

//var SwitchColors = SwitchDefaults.colors(
//    checkedThumbColor = Color(0xFF1971E3), //secondaryVariant
//    checkedTrackColor = Color(0xFFC4DBF9),
//    uncheckedThumbColor = Color(0xFFECECEC), //surface
//    uncheckedTrackColor = Color(0xFF979797) // onSurface
//)
//val switchColorDark = SwitchDefaults.colors(
//    checkedThumbColor = Color(0xFF86B0F3),
//    checkedTrackColor = Color(0xFF29364B),
//    uncheckedThumbColor = Color(0xFFB9B9B9),
//    uncheckedTrackColor = Color(0xFF383838)
//)

@Composable
fun TrainDriverTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors: Colors
    if (darkTheme) {
        colors = DarkColorPalette
        BackgroundIcon = BackgroundIconDark
        BackgroundFirst = Color(0xFF242424)
        BackgroundSecond = Color(0xFF3D3D3D)
        ColorClickableText = Color(0xFF7192D5)
    } else {
        colors = LightColorPalette
        BackgroundIcon = BackgroundIconLight
        BackgroundFirst = Color(0xFF9E9E9E)
        BackgroundSecond = Color(0xFFEEEEEE)
        ColorClickableText = Color(0xFF0000FF)
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
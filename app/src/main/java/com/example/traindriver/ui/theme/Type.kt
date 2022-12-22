package com.example.traindriver.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

val Typography = Typography(
    defaultFontFamily = overpassFontFamily,
    h1 = TextStyle(
        fontSize = 39.06.sp,
        fontStyle = FontStyle.Normal,
        fontFamily = overpassFontFamily,
        fontWeight = FontWeight.Thin,
        textAlign = TextAlign.Center
    ),
    h2 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 31.25.sp,
        textAlign = TextAlign.Center
    ),
    h3 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 25.sp,
    ),
    body1 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp
    ),
    body2 = TextStyle(
        fontSize = 18.sp,
        fontFamily = overpassFontFamily,
        fontWeight = FontWeight.Light
    ),
    button = TextStyle(
        fontFamily = overpassFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp
    ),
    caption = TextStyle(
        fontSize = 18.sp,
        fontFamily = overpassFontFamily,
        fontWeight = FontWeight.Light
    ),
    overline = TextStyle(
        fontSize = 18.sp,
        fontFamily = overpassFontFamily,
        fontWeight = FontWeight.Light,
        fontStyle = FontStyle(1),
    )
)
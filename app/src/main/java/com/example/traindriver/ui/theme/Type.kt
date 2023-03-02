package com.example.traindriver.ui.theme

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp

val Typography = Typography(
    defaultFontFamily = overpassFontFamily,
    h1 = TextStyle(
        fontSize = 39.06.sp,
        fontStyle = FontStyle.Normal,
        fontWeight = FontWeight.Thin,
        textAlign = TextAlign.Center
    ),
    h2 = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 31.25.sp,
        textAlign = TextAlign.Center
    ),
    h3 = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 25.sp,
    ),
    subtitle1 = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
    ),
    subtitle2 = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
    ),
    body1 = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp
    ),
    body2 = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal,
    ),
    button = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp
    ),
    caption = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Light
    )
)

@Composable
@Preview
private fun TypographyPrev() {
    TrainDriverTheme {
        Column {
            Text(text = "H1 h1", style = Typography.h1)
            Text(text = "H2 h2", style = Typography.h2)
            Text(text = "H3 h3", style = Typography.h3)
            Text(text = "Subtitle1", style = Typography.subtitle1)
            Text(text = "Body1", style = Typography.body1)
            Text(text = "Body2", style = Typography.body2)
            Text(text = "Button", style = Typography.button)
            Text(text = "Caption", style = Typography.caption)
        }
    }
}
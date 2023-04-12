package com.example.traindriver.ui.theme

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp

val Typography = Typography(
//    displaySmall = TextStyle(
//        fontSize = 39.06.sp,
//        fontStyle = FontStyle.Normal,
//        fontWeight = FontWeight.Thin,
//        textAlign = TextAlign.Center
//    ),
//    headlineLarge = TextStyle(
//        fontWeight = FontWeight.Normal,
//        fontSize = 31.25.sp,
//        textAlign = TextAlign.Center
//    ),
//    headlineMedium = TextStyle(
//        fontWeight = FontWeight.Normal,
//        fontSize = 25.sp,
//    ),
//    titleMedium = TextStyle(
//        fontWeight = FontWeight.Medium,
//        fontSize = 22.sp,
//    ),
//    titleSmall = TextStyle(
//        fontWeight = FontWeight.SemiBold,
//        fontSize = 20.sp,
//    ),
//    bodyLarge = TextStyle(
//        fontWeight = FontWeight.Medium,
//        fontSize = 18.sp
//    ),
//    bodyMedium = TextStyle(
//        fontSize = 16.sp,
//        fontWeight = FontWeight.Normal,
//    ),
//    labelLarge = TextStyle(
//        fontWeight = FontWeight.Medium,
//        fontSize = 18.sp
//    ),
//    bodySmall = TextStyle(
//        fontSize = 16.sp,
//        fontWeight = FontWeight.Light
//    )
)

@Composable
@Preview
private fun TypographyPrev() {
    TrainDriverTheme {
        Column {
            Text(text = "H1 h1", style = Typography.displaySmall)
            Text(text = "H1 h1", style = Typography.displaySmall)
            Text(text = "H2 h2", style = Typography.headlineLarge)
            Text(text = "H3 h3", style = Typography.headlineMedium)
            Text(text = "Subtitle1", style = Typography.titleMedium)
            Text(text = "Body1", style = Typography.titleSmall)
            Text(text = "Body2", style = Typography.bodyLarge)
            Text(text = "Button", style = Typography.bodyMedium)
            Text(text = "Caption", style = Typography.labelLarge)
        }
    }
}
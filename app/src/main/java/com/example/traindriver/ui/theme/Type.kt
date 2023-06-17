package com.example.traindriver.ui.theme

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

val Typography = Typography()

@Composable
@Preview
private fun TypographyPrev() {
    TrainDriverTheme {
        Column {
            Text(text = "displayLarge_123", style = Typography.displayLarge)
            Text(text = "displayMedium_123", style = Typography.displayMedium)
            Text(text = "displaySmall_123", style = Typography.displaySmall)
            Text(text = "headlineLarge_123", style = Typography.headlineLarge)
            Text(text = "headlineMedium_123", style = Typography.headlineMedium)
            Text(text = "headlineSmall_123", style = Typography.headlineSmall)
            Text(text = "titleLarge_123", style = Typography.titleLarge)
            Text(text = "titleMedium_123", style = Typography.titleMedium)
            Text(text = "titleSmall_123", style = Typography.titleSmall)
            Text(text = "bodyLarge_123", style = Typography.bodyLarge)
            Text(text = "bodyMedium_123", style = Typography.bodyMedium)
            Text(text = "bodySmall_123", style = Typography.bodySmall)
            Text(text = "labelLarge_123", style = Typography.labelLarge)
            Text(text = "labelMedium_123", style = Typography.labelMedium)
            Text(text = "labelSmall_123", style = Typography.labelSmall)
        }
    }
}
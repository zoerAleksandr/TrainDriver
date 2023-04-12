package com.example.traindriver.ui.screen.main_screen.elements

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.traindriver.ui.theme.Typography

@Composable
fun MonthButton(modifier: Modifier) {
    Row(modifier = modifier) {
        Text(text = "ЯНВАРЬ", style = Typography.bodyLarge)
        Text(text = " ")
        Text(text = "2023", style = Typography.bodyLarge)
    }
}
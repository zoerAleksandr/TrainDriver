package com.example.traindriver.ui.screen.main_screen.elements

import androidx.compose.foundation.layout.Row
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun MonthButton(modifier: Modifier) {
    Row(modifier = modifier) {
        Text(text = "ЯНВАРЬ")
        Text(text = " ")
        Text(text = "2023")
    }
}
package com.example.traindriver.ui.screen.setting_screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.traindriver.ui.theme.Typography

@Composable
fun SettingScreen() {
    Scaffold(
        backgroundColor = Color.LightGray
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "SettingScreen", style = Typography.h1)
        }
    }
}
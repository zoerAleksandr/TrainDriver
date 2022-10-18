package com.example.traindriver.ui.signin_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.traindriver.ui.theme.ShapeBackground
import com.example.traindriver.ui.theme.TrainDriverTheme
import com.example.traindriver.ui.util.DarkLightPreviews

@Composable
fun Background() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight(0.45f)
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colors.primary,
                    shape = ShapeBackground.large
                )
        )
    }
}

@DarkLightPreviews
@Composable
private fun BackgroundPrev() {
    TrainDriverTheme {
        Background()
    }
}
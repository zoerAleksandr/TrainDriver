package com.example.traindriver.ui.screen.signin_screen.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.traindriver.ui.theme.BackgroundFirst
import com.example.traindriver.ui.theme.BackgroundSecond
import com.example.traindriver.ui.theme.ShapeBackground
import com.example.traindriver.ui.theme.TrainDriverTheme
import com.example.traindriver.ui.util.DarkLightPreviews

@Composable
fun Background(modifier: Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundSecond)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight(0.45f)
                .fillMaxWidth()
                .background(
                    color = BackgroundFirst,
                    shape = ShapeBackground.large
                )
        )
    }
}

@DarkLightPreviews
@Composable
private fun BackgroundPrev() {
    TrainDriverTheme {
        Background(
            Modifier
                .fillMaxSize()
                .background(BackgroundSecond)
        )
    }
}
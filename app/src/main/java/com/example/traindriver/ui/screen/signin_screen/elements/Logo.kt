package com.example.traindriver.ui.screen.signin_screen.elements

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.traindriver.ui.theme.TrainDriverTheme
import com.example.traindriver.ui.theme.Typography
import com.example.traindriver.ui.util.DarkLightPreviews
import com.example.traindriver.ui.util.FontScalePreviews

@Composable
fun Logo(modifier: Modifier) {
    Text(
        modifier = modifier,
        text = "Машинистам",
        style = Typography.h1,
        color = MaterialTheme.colors.onSecondary
    )
}

@Composable
@DarkLightPreviews
@FontScalePreviews
private fun StartScreenPrev() {
    TrainDriverTheme {
        Logo(modifier = Modifier.fillMaxWidth())
    }
}
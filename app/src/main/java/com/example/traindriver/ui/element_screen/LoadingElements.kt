package com.example.traindriver.ui.element_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import com.example.traindriver.R
import com.example.traindriver.ui.theme.ShapeBackground
import com.example.traindriver.ui.theme.TrainDriverTheme
import com.example.traindriver.ui.util.DarkLightPreviews

@Composable
fun LoadingElement() {
    Box(
        modifier = Modifier
            .fillMaxSize(),
//            .background(color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
    ) {
        TrainDriverProgressBar()
    }
}

@Composable
fun TrainDriverProgressBar(modifier: Modifier = Modifier) {
    CircularProgressIndicator(
        modifier = modifier
            .size(dimensionResource(id = R.dimen.size_background_progress_bar))
            .background(
                shape = ShapeBackground.medium,
                color = MaterialTheme.colorScheme.surface
            )
            .padding(dimensionResource(id = R.dimen.primary_padding_between_view)),
        color = MaterialTheme.colorScheme.secondary,
        strokeWidth = dimensionResource(id = R.dimen.stroke_width_progress_bar)
    )
}

@DarkLightPreviews
@Composable
private fun LoadingElementPrev() {
    TrainDriverTheme {
        LoadingElement()
    }
}
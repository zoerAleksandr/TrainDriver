package com.example.traindriver.ui.element_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun VerticalDividerTrainDriver(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.secondary,
    thickness: Dp = 1.dp
){
    Box(modifier = modifier
        .fillMaxHeight()
        .width(thickness)
        .background(color = color))
}


@Composable
fun HorizontalDividerTrainDriver(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f),
    thickness: Dp = 1.dp
){
    Box(modifier = modifier
        .fillMaxWidth()
        .height(thickness)
        .background(color = color))
}




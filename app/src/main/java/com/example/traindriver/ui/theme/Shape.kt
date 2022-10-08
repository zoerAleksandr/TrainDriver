package com.example.traindriver.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Shapes
import androidx.compose.ui.unit.dp

val ShapeInputData = Shapes(
    medium = RoundedCornerShape(25.dp)
)

val ShapeBackground = Shapes(
    small = RoundedCornerShape(5.dp),
    medium = RoundedCornerShape(
        bottomStart = 35.dp,
        bottomEnd = 35.dp,
        topStart = 0.dp,
        topEnd = 0.dp
    )
)
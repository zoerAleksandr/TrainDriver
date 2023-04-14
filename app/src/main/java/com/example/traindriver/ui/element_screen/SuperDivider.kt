package com.example.traindriver.ui.element_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun SuperDivider(
    colorBackground: Color = MaterialTheme.colorScheme.background,
    colorDivider: Color = MaterialTheme.colorScheme.onBackground,
    thickness: Dp = 20.dp
){
    Column(modifier = Modifier
        .fillMaxWidth()
        .height(thickness)
        .background(colorDivider)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(color = colorBackground, shape = ShapeDividerElementTop.medium)
        )
        Box(modifier = Modifier
            .weight(1f)
            .background(Color.Transparent))
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(color = colorBackground, shape = ShapeDividerElementBottom.medium)
        )
    }
}

private val CORNER_ROUND_DIVIDER = 12.dp
val ShapeDividerElementTop = Shapes(
    medium = RoundedCornerShape(
        bottomStart = CORNER_ROUND_DIVIDER,
        bottomEnd = CORNER_ROUND_DIVIDER,
        topStart = 0.dp,
        topEnd = 0.dp
    )
)

val ShapeDividerElementBottom = Shapes(
    medium = RoundedCornerShape(
        bottomStart = 0.dp,
        bottomEnd = 0.dp,
        topStart = CORNER_ROUND_DIVIDER,
        topEnd = CORNER_ROUND_DIVIDER
    )
)
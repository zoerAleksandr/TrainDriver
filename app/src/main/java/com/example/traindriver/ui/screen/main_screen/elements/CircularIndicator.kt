package com.example.traindriver.ui.screen.main_screen.elements

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.traindriver.ui.theme.SpecialColor
import com.example.traindriver.ui.theme.TrainDriverTheme
import com.example.traindriver.ui.util.DarkLightPreviews
import com.example.traindriver.ui.util.FontScalePreviews

@Composable
fun CircularIndicator(
    canvasSize: Dp = 300.dp,
    valueHour: Int = 0,
    valueMinute: Int = 0,
    maxIndicatorValue: Int = 180,
    backgroundIndicatorColor: Color = MaterialTheme.colors.surface,
    backgroundIndicatorStrokeWidth: Float = 50f,
    foregroundIndicatorColor: Color = SpecialColor,
    foregroundIndicatorStrokeWidth: Float = 70f,
) {
    var allowedIndicatorValue by remember {
        mutableStateOf(maxIndicatorValue)
    }

    allowedIndicatorValue = if (valueHour <= maxIndicatorValue) {
        valueHour
    } else {
        maxIndicatorValue
    }

    val animatorIndicatorValue = remember { Animatable(initialValue = 0f) }

    LaunchedEffect(key1 = allowedIndicatorValue) {
        animatorIndicatorValue.animateTo(allowedIndicatorValue.toFloat())
    }

    val percent = (animatorIndicatorValue.value / maxIndicatorValue) * 100

    val sweepAngle by animateFloatAsState(
        targetValue = (2.4 * percent).toFloat(),
        animationSpec = tween(1000)
    )

    ConstraintLayout(
        modifier = Modifier
            .size(canvasSize)
            .drawBehind {
                val componentSize = size / 1.4f
                backgroundIndicator(
                    componentSize = componentSize,
                    indicatorColor = backgroundIndicatorColor,
                    indicatorStrokeWidth = backgroundIndicatorStrokeWidth
                )
                foregroundIndicator(
                    sweepAngle = sweepAngle,
                    componentSize = componentSize,
                    indicatorColor = foregroundIndicatorColor,
                    indicatorStrokeWidth = foregroundIndicatorStrokeWidth
                )
            },
    ) {
        val (embeddedText, externalText) = createRefs()

        val animatedHour by animateIntAsState(
            targetValue = valueHour,
            animationSpec = tween(1000)
        )

        val animatedMinute by animateIntAsState(
            targetValue = valueMinute,
            animationSpec = tween(1000)
        )

        EmbeddedText(
            modifier = Modifier
                .constrainAs(embeddedText) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                },
            valueHour = animatedHour,
            valueMinute = animatedMinute
        )

        Text(
            modifier = Modifier
                .constrainAs(externalText) {
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                }
                .padding(bottom = canvasSize / 5),
            text = "$maxIndicatorValue",
            fontSize = MaterialTheme.typography.h4.fontSize
        )
    }

}

fun DrawScope.backgroundIndicator(
    componentSize: Size,
    indicatorColor: Color,
    indicatorStrokeWidth: Float
) {
    drawArc(
        size = componentSize,
        color = indicatorColor,
        startAngle = 150f,
        sweepAngle = 240f,
        useCenter = false,
        style = Stroke(
            width = indicatorStrokeWidth,
            cap = StrokeCap.Round
        ),
        topLeft = Offset(
            x = (size.width - componentSize.width) / 2f,
            y = (size.height - componentSize.height) / 2f
        )
    )
}

fun DrawScope.foregroundIndicator(
    sweepAngle: Float,
    componentSize: Size,
    indicatorColor: Color,
    indicatorStrokeWidth: Float
) {
    drawArc(
        size = componentSize,
        color = indicatorColor,
        startAngle = 150f,
        sweepAngle = sweepAngle,
        useCenter = false,
        style = Stroke(
            width = indicatorStrokeWidth,
            cap = StrokeCap.Round
        ),
        topLeft = Offset(
            x = (size.width - componentSize.width) / 2f,
            y = (size.height - componentSize.height) / 2f
        )
    )
}

@Composable
fun EmbeddedText(
    modifier: Modifier,
    valueHour: Int,
    valueMinute: Int
) {
    val hour: String = if (valueHour.toString().length == 1) {
        "0$valueHour"
    } else {
        valueHour.toString()
    }

    val minute: String = if (valueMinute.toString().length == 1) {
        "0$valueMinute"
    } else {
        valueMinute.toString()
    }

    Text(
        modifier = modifier,
        text = "$hour : $minute",
        fontSize = MaterialTheme.typography.h1.fontSize,
        fontWeight = FontWeight.Bold
    )
}

@Composable
@FontScalePreviews
@DarkLightPreviews
private fun prevCircularIndicator() {
    TrainDriverTheme {
        CircularIndicator(valueHour = 1)
    }
}
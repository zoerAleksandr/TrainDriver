package com.example.traindriver.ui.util

import androidx.compose.ui.graphics.Color

fun changeAlphaWithScroll(offset: Float, initColor: Color): Color {
    val offsetMax = 1500
    val offsetMin = 250

    val alphaMax = 0
    val alphaMin = 1

    val offsetRange = offsetMax - offsetMin
    val alphaRange = alphaMax - alphaMin

    var alpha = (((offset - offsetMin) * alphaRange) / offsetRange) + alphaMin

    if (alpha > 1F) alpha = 1F
    if (alpha < 0F) alpha = 0F

    return initColor.copy(alpha = alpha)
}
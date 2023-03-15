package com.example.traindriver.ui.util

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import com.example.traindriver.ui.theme.Typography

@Composable
fun ClickableTextTrainDriver(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    style: TextStyle = Typography.button,
    softWrap: Boolean = true,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    onClick: (Int) -> Unit
) {
    val layoutResult = remember { mutableStateOf<TextLayoutResult?>(null) }
    val pressIndicator = Modifier.pointerInput(onClick) {
        if (enabled) {
            detectTapGestures { pos ->
                layoutResult.value?.let { layoutResult ->
                    onClick(layoutResult.getOffsetForPosition(pos))
                }
            }
        }
    }
    val currentStyle = style.copy(
        color = if (enabled) {
            MaterialTheme.colors.secondaryVariant
        } else {
            MaterialTheme.colors.primaryVariant
        }
    )

    BasicText(
        text = text,
        modifier = modifier.then(pressIndicator),
        style = currentStyle,
        softWrap = softWrap,
        overflow = overflow,
        maxLines = maxLines,
        onTextLayout = {
            layoutResult.value = it
            onTextLayout(it)
        }
    )
}
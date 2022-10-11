package com.example.traindriver.ui.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText

fun transformedNumberRussia(text: AnnotatedString): TransformedText {
    val trimmed = if (text.text.length >= 12) text.text.substring(0..11) else text.text
    val annotatedString = AnnotatedString.Builder().run {
        for (i in trimmed.indices) {
            when (i) {
                2 -> {
                    append(" (${trimmed[i]}")
                }
                5 -> {
                    append(") ${trimmed[i]}")
                }
                8 -> {
                    append("-${trimmed[i]}")
                }
                10 -> {
                    append("-${trimmed[i]}")
                }
                else -> {
                    append(trimmed[i])
                }
            }
        }
        toAnnotatedString()
    }

    val offset = object : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int {
            if (offset <= 2) return offset
            if (offset <= 5) return offset + 2
            if (offset <= 8) return offset + 4
            if (offset <= 10) return offset + 5
            if (offset <= 12) return offset + 6
            return 18
        }

        override fun transformedToOriginal(offset: Int): Int {
            if (offset <= 2) return offset
            if (offset <= 7) return offset - 2
            if (offset <= 12) return offset - 4
            if (offset <= 15) return offset - 5
            if (offset <= 18) return offset - 6
            return 13
        }
    }

    return TransformedText(annotatedString, offset)
}
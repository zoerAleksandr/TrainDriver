package com.example.traindriver.ui.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText

fun transformerNumberDefault(text: AnnotatedString): TransformedText {
    /** default transformer Number = "xxxxxxxxxxxx" length = 17 */
    val trimmed = if (text.text.length >= 18) text.text.substring(0..17) else text.text
    val annotatedString = AnnotatedString.Builder().run {
        append(trimmed)
        toAnnotatedString()
    }

    val offset = object : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int {
            return offset
        }

        override fun transformedToOriginal(offset: Int): Int {
            return offset
        }
    }

    return TransformedText(annotatedString, offset)
}

fun transformedNumberRUAndKZ(text: AnnotatedString): TransformedText {
    /** example number Russia = "+7 (yyy) xxx-xx-xx" length = 12 */

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

fun transformNumberBY(text: AnnotatedString): TransformedText {
    /** example number Belarus = "+375 (yy) xxx-xx-xx" length = 13 */

    val trimmed = if (text.text.length >= 13) text.text.substring(0..12) else text.text
    val annotatedString = AnnotatedString.Builder().run {
        for (i in trimmed.indices) {
            when (i) {
                4 -> {
                    append(" (${trimmed[i]}")
                }
                6 -> {
                    append(") ${trimmed[i]}")
                }
                9 -> {
                    append("-${trimmed[i]}")
                }
                11 -> {
                    append("-${trimmed[i]}")
                }
                else -> {
                    append(trimmed[i])
                }
            }
        }
        toAnnotatedString()
    }

    val offsetMapping = object : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int {
            if (offset <= 4) return offset
            if (offset <= 6) return offset + 2
            if (offset <= 9) return offset + 4
            if (offset <= 11) return offset + 5
            if (offset <= 13) return offset + 6
            return 19
        }

        override fun transformedToOriginal(offset: Int): Int {
            if (offset <= 4) return offset
            if (offset <= 9) return offset - 2
            if (offset <= 13) return offset - 4
            if (offset <= 16) return offset - 5
            if (offset <= 19) return offset - 6
            return 13
        }
    }
    return TransformedText(annotatedString, offsetMapping)
}
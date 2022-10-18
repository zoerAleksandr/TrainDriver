package com.example.traindriver.ui.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.TransformedText

enum class LocaleState : LocaleInterface {
    RU {
        override fun prefix(): String = "+7"
        override fun maxLength(): Int = 12
        override val transformedNumber: (AnnotatedString) -> TransformedText = {
            transformedNumberRUAndKZ(it)
        }
    },
    BY {
        override fun prefix(): String = "+375"
        override fun maxLength(): Int = 13
        override val transformedNumber: (AnnotatedString) -> TransformedText = {
            transformNumberBY(it)
        }
    },
    KZ {
        override fun prefix(): String = "+7"
        override fun maxLength(): Int = 12
        override val transformedNumber: (AnnotatedString) -> TransformedText = {
            transformedNumberRUAndKZ(it)
        }
    },
    OTHER {
        override fun prefix(): String = ""
        override fun maxLength(): Int = 17
        override val transformedNumber: (AnnotatedString) -> TransformedText = {
            transformerNumberDefault(it)
        }
    }
}

interface LocaleInterface {
    fun prefix(): String
    fun maxLength(): Int
    val transformedNumber: (AnnotatedString) -> TransformedText
}
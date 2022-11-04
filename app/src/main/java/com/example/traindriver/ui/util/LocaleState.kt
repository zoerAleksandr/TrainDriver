package com.example.traindriver.ui.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.TransformedText
import com.example.traindriver.R

enum class LocaleState : LocaleInterface {
    RU {
        override fun prefix(): String = "+7"
        override fun maxLength(): Int = 12
        override val transformedNumber: (AnnotatedString) -> TransformedText = {
            transformedNumberRUAndKZ(it)
        }
        override val icon = R.drawable.russia
        override val contentDescription = "Россия"
    },
    BY {
        override fun prefix(): String = "+375"
        override fun maxLength(): Int = 13
        override val transformedNumber: (AnnotatedString) -> TransformedText = {
            transformNumberBY(it)
        }
        override val icon = R.drawable.belarus
        override val contentDescription = "Беларусь"
    },
    KZ {
        override fun prefix(): String = "+7"
        override fun maxLength(): Int = 12
        override val transformedNumber: (AnnotatedString) -> TransformedText = {
            transformedNumberRUAndKZ(it)
        }
        override val icon = R.drawable.kazakhstan
        override val contentDescription = "Казахстан"
    },
    OTHER {
        override fun prefix(): String = ""
        override fun maxLength(): Int = 17
        override val transformedNumber: (AnnotatedString) -> TransformedText = {
            transformerNumberDefault(it)
        }
        override val icon = R.drawable.empty_flag
        override val contentDescription = "Страна не определена"
    }
}

interface LocaleInterface {
    fun prefix(): String
    fun maxLength(): Int
    val transformedNumber: (AnnotatedString) -> TransformedText
    val icon: Int
    val contentDescription: String
}
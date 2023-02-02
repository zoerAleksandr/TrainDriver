package com.example.traindriver.domain.entity

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.TransformedText
import com.example.traindriver.R
import com.example.traindriver.ui.util.transformNumberBY
import com.example.traindriver.ui.util.transformedNumberRUAndKZ
import com.example.traindriver.ui.util.transformerNumberDefault

sealed class LocaleUser : LocaleInterface {
    object RU : LocaleUser() {
        override fun prefix(): String = "+7"
        override fun maxLength(): Int = 12
        override val transformedNumber: (AnnotatedString) -> TransformedText = {
            transformedNumberRUAndKZ(it)
        }
        override val icon = R.drawable.russia
        override val contentDescription = "Россия"
    }

    object BY : LocaleUser() {
        override fun prefix(): String = "+375"
        override fun maxLength(): Int = 13
        override val transformedNumber: (AnnotatedString) -> TransformedText = {
            transformNumberBY(it)
        }
        override val icon = R.drawable.belarus
        override val contentDescription = "Беларусь"
    }

    object KZ : LocaleUser() {
        override fun prefix(): String = "+7"
        override fun maxLength(): Int = 12
        override val transformedNumber: (AnnotatedString) -> TransformedText = {
            transformedNumberRUAndKZ(it)
        }
        override val icon = R.drawable.kazakhstan
        override val contentDescription = "Казахстан"
    }

    object OTHER : LocaleUser() {
        override fun prefix(): String = ""
        override fun maxLength(): Int = 17
        override val transformedNumber: (AnnotatedString) -> TransformedText = {
            transformerNumberDefault(it)
        }
        override val icon = R.drawable.empty_flag
        override val contentDescription = "Другая страна"
    }
}

interface LocaleInterface {
    fun prefix(): String
    fun maxLength(): Int
    val transformedNumber: (AnnotatedString) -> TransformedText
    val icon: Int
    val contentDescription: String
}
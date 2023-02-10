package com.example.traindriver.ui.util

import android.content.res.Resources
import com.example.traindriver.R
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object Month: KoinComponent {
    private val res: Resources by inject()

    val JANUARY = res.getString(R.string.january)
    val FEBRUARY = res.getString(R.string.february)
    val MARCH = res.getString(R.string.march)
    val APRIL = res.getString(R.string.april)
    val MAY = res.getString(R.string.may)
    val JUNE = res.getString(R.string.june)
    val JULY = res.getString(R.string.july)
    val AUGUST = res.getString(R.string.august)
    val SEPTEMBER = res.getString(R.string.september)
    val OCTOBER = res.getString(R.string.october)
    val NOVEMBER = res.getString(R.string.november)
    val DECEMBER = res.getString(R.string.december)
}
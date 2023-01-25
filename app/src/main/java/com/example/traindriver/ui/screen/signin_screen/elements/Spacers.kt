package com.example.traindriver.ui.screen.signin_screen.elements

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import com.example.traindriver.R

@Composable
fun PrimarySpacer() {
    Spacer(
        modifier = Modifier.padding(dimensionResource(id = R.dimen.primary_padding_between_view))
    )
}

@Composable
fun SecondarySpacer() {
    Spacer(
        modifier = Modifier.padding(dimensionResource(id = R.dimen.secondary_padding_between_view))
    )
}
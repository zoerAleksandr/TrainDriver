package com.example.traindriver.ui.element_screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarData
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun TopSnackbar(snackBarData: SnackbarData) {
    Box(modifier = Modifier.fillMaxSize()) {
        Snackbar(
            modifier = Modifier
                .align(Alignment.TopCenter),
            snackbarData = snackBarData,
            backgroundColor = MaterialTheme.colors.background,
            contentColor = MaterialTheme.colors.onBackground
        )
    }
}
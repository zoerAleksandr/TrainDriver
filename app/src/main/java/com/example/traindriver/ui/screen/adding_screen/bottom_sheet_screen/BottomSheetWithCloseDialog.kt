package com.example.traindriver.ui.screen.adding_screen.bottom_sheet_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.example.traindriver.R

@Composable
fun BottomSheetWithCloseDialog(closeSheet: () -> Unit, content: @Composable () -> Unit) {
    Box(modifier = Modifier
        .background(color = MaterialTheme.colors.background)
        .fillMaxWidth()
        .fillMaxHeight(0.96f), contentAlignment = Alignment.TopStart) {
        IconButton(
            modifier = Modifier
                .padding(start = 8.dp)
                .size(dimensionResource(id = R.dimen.min_size_view)),
            onClick = closeSheet
        ) {
            Icon(Icons.Sharp.Close, contentDescription = null, tint = MaterialTheme.colors.primary)
        }
        content()
    }
}
package com.example.traindriver.ui.element_screen

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import com.example.traindriver.R

@Composable
fun HandleBottomSheet(){
    Divider(
        modifier = Modifier
            .padding(top = dimensionResource(id = R.dimen.secondary_padding_between_view))
            .size(
                width = dimensionResource(id = R.dimen.sheet_handle_width),
                height = dimensionResource(id = R.dimen.sheet_handle_height)
            )
    )
}
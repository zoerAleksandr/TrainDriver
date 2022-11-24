package com.example.traindriver.ui.screen.password_conf_screen

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.example.traindriver.R
import com.example.traindriver.ui.theme.ShapeBackground
import com.example.traindriver.ui.theme.TrainDriverTheme
import com.example.traindriver.ui.util.DarkLightPreviews
import com.example.traindriver.ui.util.FontScalePreviews

@Composable
fun PasswordEditText(modifier: Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center
    ) {
        val size = dimensionResource(id = R.dimen.min_size_view)
        val padding = 8.dp
        BasicTextField(
            modifier = Modifier
                .padding(end = padding)
                .width(size)
                .height(size)
                .border(
                    color = MaterialTheme.colors.onSurface,
                    width = dimensionResource(id = R.dimen.width_border_input_field),
                    shape = ShapeBackground.small
                ),
            value = "",
            onValueChange = {}
        )
        BasicTextField(
            modifier = Modifier
                .padding(end = padding)
                .width(size)
                .height(size)
                .border(
                    color = MaterialTheme.colors.onSurface,
                    width = dimensionResource(id = R.dimen.width_border_input_field),
                    shape = ShapeBackground.small
                ),
            value = "",
            onValueChange = {}
        )
        BasicTextField(
            modifier = Modifier
                .padding(end = padding)
                .width(size)
                .height(size)
                .border(
                    color = MaterialTheme.colors.onSurface,
                    width = dimensionResource(id = R.dimen.width_border_input_field),
                    shape = ShapeBackground.small
                ),
            value = "",
            onValueChange = {}
        )
        BasicTextField(
            modifier = Modifier
                .padding(end = padding)
                .width(size)
                .height(size)
                .border(
                    color = MaterialTheme.colors.onSurface,
                    width = dimensionResource(id = R.dimen.width_border_input_field),
                    shape = ShapeBackground.small
                ),
            value = "",
            onValueChange = {}
        )
        BasicTextField(
            modifier = Modifier
                .padding(end = padding)
                .width(size)
                .height(size)
                .border(
                    color = MaterialTheme.colors.onSurface,
                    width = dimensionResource(id = R.dimen.width_border_input_field),
                    shape = ShapeBackground.small
                ),
            value = "",
            onValueChange = {}
        )
        BasicTextField(
            modifier = Modifier
                .width(size)
                .height(size)
                .border(
                    color = MaterialTheme.colors.onSurface,
                    width = dimensionResource(id = R.dimen.width_border_input_field),
                    shape = ShapeBackground.small
                ),
            value = "",
            onValueChange = {}
        )
    }
}

@Composable
@DarkLightPreviews
@FontScalePreviews
private fun PasswordPreview() {
    TrainDriverTheme {
        PasswordEditText(modifier = Modifier)
    }
}
package com.example.traindriver.ui.element_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import com.example.traindriver.R
import com.example.traindriver.ui.theme.ShapeBackground
import com.example.traindriver.ui.theme.Typography
import com.example.traindriver.ui.theme.overpassFontFamily
import com.example.traindriver.ui.util.transformedNumberRussia

@Composable
fun CustomTextField(
    placeholderText: String,
    data: MutableState<String>
) {
    BasicTextField(
        modifier = Modifier
            .fillMaxWidth()
            .height(dimensionResource(id = R.dimen.min_size_view))
            .background(
                color = MaterialTheme.colors.surface,
                shape = ShapeBackground.small
            )
            .border(
                color = MaterialTheme.colors.onSurface,
                width = dimensionResource(id = R.dimen.width_border_input_field),
                shape = ShapeBackground.small
            ),
        value = data.value,
        onValueChange = {
            if (it.length <= 12) data.value = it
        },
        visualTransformation = { transformedNumberRussia(it) },
        textStyle = TextStyle(
            color = MaterialTheme.colors.onBackground,
            fontSize = 18.sp,
            fontFamily = overpassFontFamily,
            fontWeight = FontWeight.Light
        ),
        cursorBrush = SolidColor(MaterialTheme.colors.onSurface),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .padding(start = dimensionResource(id = R.dimen.padding_start_input_field)),
                contentAlignment = Alignment.CenterStart
            ) {
                if (data.value.isEmpty()) {
                    Text(
                        text = placeholderText,
                        style = Typography.caption,
                        color = MaterialTheme.colors.onSurface
                    )
                }
                innerTextField()
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Phone,
            imeAction = ImeAction.Go
        ),
        keyboardActions = KeyboardActions(
            // TODO
        ),
        singleLine = true
    )
}
package com.example.traindriver.ui.element_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.traindriver.R
import com.example.traindriver.ui.theme.ShapeBackground
import com.example.traindriver.ui.theme.TrainDriverTheme
import com.example.traindriver.ui.theme.Typography
import com.example.traindriver.ui.theme.overpassFontFamily
import com.example.traindriver.ui.util.DarkLightPreviews
import com.example.traindriver.ui.util.FieldIsFilled
import com.example.traindriver.ui.util.FontScalePreviews
import com.example.traindriver.ui.util.LocaleState

@Composable
fun CustomTextField(
    placeholderText: String,
    data: MutableState<String>,
    localeState: LocaleState,
    isFilledCallback: FieldIsFilled? = null
) {
    ConstraintLayout(
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
            )
    ) {
        val (button, editText) = createRefs()
        Box(
            modifier = Modifier
                .constrainAs(button) {
                    start.linkTo(parent.start, margin = 0.dp)
                }
                .clickable {
                    // TODO выпадающий список
                }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(id = localeState.icon),
                    contentDescription = localeState.contentDescription,
                    modifier = Modifier
                        .padding(start = 8.dp, top = 8.dp, bottom = 8.dp)
                )
                Image(
                    painter = painterResource(id = R.drawable.ic_arrow_drop_down_24),
                    contentDescription = null
                )
            }
        }
        BasicTextField(
            modifier = Modifier
                .fillMaxHeight()
                .constrainAs(editText) {
                    start.linkTo(button.end)
                },
            value = data.value,
            onValueChange = {
                if (it.length <= localeState.maxLength()) data.value = it
                isFilledCallback?.isFilled(it.length >= localeState.maxLength())
            },
            visualTransformation = localeState.transformedNumber,
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
}

@Composable
@DarkLightPreviews
@FontScalePreviews
private fun StartScreenPrev() {
    TrainDriverTheme {
        CustomTextField(
            "Номер телефона",
            mutableStateOf("+7"),
            LocaleState.RU,
        )
    }
}
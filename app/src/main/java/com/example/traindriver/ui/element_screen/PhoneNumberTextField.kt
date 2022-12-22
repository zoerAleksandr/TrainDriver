package com.example.traindriver.ui.element_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.example.traindriver.ui.util.LocaleUser
import com.example.traindriver.ui.util.LocaleUser.OTHER

@Composable
fun NumberPhoneTextField(
    placeholderText: String,
    numberState: MutableState<String>,
    localeUser: MutableState<LocaleUser>,
    allowEntry: MutableState<Boolean>,
    isFilledCallback: FieldIsFilled? = null
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .height(dimensionResource(id = R.dimen.min_size_view))
            .background(
                color = Color.Transparent,
            )
            .border(
                color = MaterialTheme.colors.onBackground,
                width = dimensionResource(id = R.dimen.width_border_input_field),
                shape = ShapeBackground.medium
            )
    ) {
        val (button, editText) = createRefs()
        val dropDownExpanded = rememberSaveable {
            mutableStateOf(false)
        }
        DropdownMenu(
            expanded = dropDownExpanded.value,
            onDismissRequest = { dropDownExpanded.value = false },
            modifier = Modifier
                .fillMaxWidth(0.72f)
        ) {
            getAllLocaleExcept(localeUser.value).forEach {
                DropDownLocaleItem(
                    state = it,
                    currentLocale = localeUser,
                    numberState = numberState,
                    allowEntry = allowEntry,
                    dropDownExpanded = dropDownExpanded
                )
                if (it == OTHER) {
                    Divider(
                        color = MaterialTheme.colors.onSurface,
                        startIndent = 12.dp
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .constrainAs(button) {
                    start.linkTo(parent.start, margin = 12.dp)
                }
                .clickable { dropDownExpanded.value = true }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(id = localeUser.value.icon),
                    contentDescription = localeUser.value.contentDescription,
                    modifier = Modifier
                        .padding(start = 8.dp, top = 8.dp, bottom = 8.dp)
                )
            }
        }
        BasicTextField(
            modifier = Modifier
                .fillMaxHeight()
                .constrainAs(editText) {
                    start.linkTo(button.end, margin = 12.dp)
                },
            value = numberState.value,
            onValueChange = {
                if (it.length <= localeUser.value.maxLength()) numberState.value = it
                isFilledCallback?.isFilled(it.length >= localeUser.value.maxLength())
            },
            visualTransformation = localeUser.value.transformedNumber,
            textStyle = TextStyle(
                color = MaterialTheme.colors.primary,
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
                    if (numberState.value.isEmpty()) {
                        Text(
                            text = placeholderText,
                            style = Typography.caption,
                            color = MaterialTheme.colors.primaryVariant
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
                onGo = {
                    // TODO
                }
            ),
            singleLine = true
        )
    }
}

@Composable
private fun DropDownLocaleItem(
    state: LocaleUser,
    currentLocale: MutableState<LocaleUser>,
    numberState: MutableState<String>,
    allowEntry: MutableState<Boolean>,
    dropDownExpanded: MutableState<Boolean>
) {
    DropdownMenuItem(
        onClick = {
            currentLocale.value = state
            numberState.value = state.prefix()
            allowEntry.value = (state == OTHER)
            dropDownExpanded.value = false
        },
        modifier = Modifier
            .height(dimensionResource(id = R.dimen.min_size_view))
    ) {
        Image(
            painter = painterResource(id = state.icon),
            contentDescription = state.contentDescription,
            modifier = Modifier
                .padding(top = 8.dp, bottom = 8.dp)
        )
        Text(text = state.contentDescription, modifier = Modifier.padding(start = 10.dp))
        Text(text = state.prefix(), modifier = Modifier.padding(start = 10.dp))
    }
}

inline fun <reified LocaleState> getAllLocaleExcept(except: LocaleState): List<LocaleState> {
    val list = mutableListOf<LocaleState>()
    LocaleState::class.sealedSubclasses
        .map {
            if (it.simpleName != except!!::class.simpleName) {
                if (it.simpleName == OTHER::class.simpleName) {
                    list.add(0, it.objectInstance as LocaleState)
                } else {
                    list.add(it.objectInstance as LocaleState)
                }
            }
        }
    return list

}

@Composable
@DarkLightPreviews
@FontScalePreviews
private fun StartScreenPrev() {
    TrainDriverTheme {
        NumberPhoneTextField(
            "Номер телефона",
            mutableStateOf("+7"),
            mutableStateOf(LocaleUser.RU),
            mutableStateOf(true)
        )
    }
}

@Composable
@DarkLightPreviews
@FontScalePreviews
private fun DropDownPrev() {
    TrainDriverTheme {
        DropDownLocaleItem(
            LocaleUser.RU,
            mutableStateOf(LocaleUser.RU),
            mutableStateOf("+7"),
            mutableStateOf(true),
            mutableStateOf(false)
        )
    }
}

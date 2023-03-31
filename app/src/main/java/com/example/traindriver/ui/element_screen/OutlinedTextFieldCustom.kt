package com.example.traindriver.ui.element_screen

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.traindriver.ui.theme.Typography
import com.example.traindriver.R

@Composable
fun OutlinedTextFieldCustom(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    labelText: String? = null,
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions(),
    onClickTrailingIcon: (() -> Unit)
) {
    val colors = TextFieldDefaults.outlinedTextFieldColors(
        textColor = MaterialTheme.colors.primary,
        unfocusedBorderColor = MaterialTheme.colors.secondary
    )

    OutlinedTextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        colors = colors,
        label = { Text(text = labelText ?: "", style = Typography.body1) },
        singleLine = singleLine,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        trailingIcon = {
            IconButton(
                onClick =  onClickTrailingIcon
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_close_24),
                    contentDescription = null
                )
            }
        }
    )
}

@Composable
fun OutlinedTextFieldCustom(
    modifier: Modifier = Modifier,
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    labelText: String? = null,
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions()
) {
    val colors = TextFieldDefaults.outlinedTextFieldColors(
        textColor = MaterialTheme.colors.primary,
        unfocusedBorderColor = MaterialTheme.colors.secondary
    )

    OutlinedTextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        colors = colors,
        label = { Text(text = labelText ?: "", style = Typography.body1) },
        singleLine = singleLine,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions
    )
}

package com.example.traindriver.ui.element_screen

import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import com.example.traindriver.ui.theme.Typography

@Composable
fun OutlinedTextFieldCustom(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    labelText: String? = null
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
        label = { Text(text = labelText ?: "", style = Typography.body1) }
    )
}

@Composable
fun OutlinedTextFieldCustom(
    modifier: Modifier = Modifier,
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    labelText: String? = null
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
        label = { Text(text = labelText ?: "", style = Typography.body1) }
    )
}

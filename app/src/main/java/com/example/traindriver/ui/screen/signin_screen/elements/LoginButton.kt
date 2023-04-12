package com.example.traindriver.ui.screen.signin_screen.elements

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.traindriver.R
import com.example.traindriver.ui.theme.*
import com.example.traindriver.ui.util.DarkLightPreviews
import com.example.traindriver.ui.util.FontScalePreviews

@Composable
fun LoginButton(
    enabled: Boolean,
    isLoading: Boolean,
    onClick: () -> Unit
) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .height(dimensionResource(id = R.dimen.min_size_view)),
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = SpecialColor,
            disabledContainerColor = SpecialDisableColor
        ),
        shape = ShapeButton.medium,
        enabled = enabled
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(dimensionResource(id = R.dimen.min_size_view) * 0.6f),
                color = Color.White,
                strokeWidth = 3.dp
            )
        } else {
            Text(
                text = stringResource(id = R.string.text_entrance_button),
                color = MaterialTheme.colorScheme.onSecondary,
                style = Typography.bodyMedium
            )
        }
    }
}

@Composable
@DarkLightPreviews
@FontScalePreviews
private fun StartScreenPrev() {
    TrainDriverTheme {
        LoginButton(enabled = false, isLoading = false, {})
    }
}
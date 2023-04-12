package com.example.traindriver.ui.screen.signin_screen.elements

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.traindriver.R
import com.example.traindriver.ui.theme.ShapeButton
import com.example.traindriver.ui.theme.Typography

@Composable
fun SignInGoogleButton(
    isLoading: Boolean,
    onClick: () -> Unit
) {
    Button(
        modifier = Modifier.fillMaxWidth(),
        shape = ShapeButton.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        enabled = !isLoading,
        onClick = onClick
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
                text = stringResource(id = R.string.text_entrance_with_google),
                color = MaterialTheme.colorScheme.primary,
                style = Typography.bodyMedium
            )
        }
    }
}

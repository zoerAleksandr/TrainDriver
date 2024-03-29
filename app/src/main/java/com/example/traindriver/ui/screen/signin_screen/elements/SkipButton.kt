package com.example.traindriver.ui.screen.signin_screen.elements

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.example.traindriver.R
import com.example.traindriver.ui.theme.TrainDriverTheme
import com.example.traindriver.ui.theme.Typography
import com.example.traindriver.ui.util.DarkLightPreviews
import com.example.traindriver.ui.util.FontScalePreviews

@Composable
fun SkipButton(modifier: Modifier) {
    Text(
        modifier = modifier,
        text = stringResource(id = R.string.text_skip_button),
        style = Typography.bodyMedium.copy(fontSize = 18.sp),
        color = MaterialTheme.colorScheme.onBackground
    )
}
@Composable
@DarkLightPreviews
@FontScalePreviews
private fun StartScreenPrev() {
    TrainDriverTheme {
        SkipButton(modifier = Modifier)
    }
}
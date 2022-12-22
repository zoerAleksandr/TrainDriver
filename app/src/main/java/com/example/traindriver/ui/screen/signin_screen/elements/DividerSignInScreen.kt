package com.example.traindriver.ui.screen.signin_screen.elements

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.example.traindriver.R
import com.example.traindriver.ui.theme.TrainDriverTheme
import com.example.traindriver.ui.theme.Typography
import com.example.traindriver.ui.util.DarkLightPreviews
import com.example.traindriver.ui.util.FontScalePreviews

@Composable
fun DividerSignInScreen() {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Divider(
            modifier = Modifier
                .weight(1f)
                .padding(end = dimensionResource(id = R.dimen.padding_divider)),
            color = MaterialTheme.colors.secondary
        )
        Text(
            modifier = Modifier.wrapContentWidth(),
            textAlign = TextAlign.Justify,
            text = stringResource(id = R.string.divider_text),
            style = Typography.body2,
            color = MaterialTheme.colors.secondary
        )
        Divider(
            modifier = Modifier
                .weight(1f)
                .padding(start = dimensionResource(id = R.dimen.padding_divider)),
            color = MaterialTheme.colors.secondary
        )

    }
}

@Composable
@DarkLightPreviews
@FontScalePreviews
private fun StartScreenPrev() {
    TrainDriverTheme {
        DividerSignInScreen()
    }
}
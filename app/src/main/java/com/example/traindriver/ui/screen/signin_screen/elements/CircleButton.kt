package com.example.traindriver.ui.screen.signin_screen.elements

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.traindriver.R
import com.example.traindriver.ui.theme.TrainDriverTheme
import com.example.traindriver.ui.util.DarkLightPreviews

@Composable
fun CircleButton(resId: Int, contentDescription: String?, action: () -> Unit) {
    Button(
        modifier = Modifier.size(dimensionResource(id = R.dimen.min_size_view)),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
        onClick = action
    ) {
        Image(
            painter = painterResource(id = resId),
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
@DarkLightPreviews
private fun StartScreenPrev() {
    TrainDriverTheme {
        CircleButton(R.drawable.ic_launcher_foreground,
            stringResource(id = R.string.cont_desc_vk_button), {}
        )
    }
}
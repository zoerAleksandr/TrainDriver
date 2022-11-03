package com.example.traindriver.ui.screen.signin_screen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.traindriver.R
import com.example.traindriver.data.auth.SignInMethod
import com.example.traindriver.ui.element_screen.CustomTextField
import com.example.traindriver.ui.screen.splash_screen.SplashViewModel
import com.example.traindriver.ui.theme.ShapeInputData
import com.example.traindriver.ui.theme.TrainDriverTheme
import com.example.traindriver.ui.theme.Typography
import com.example.traindriver.ui.util.DarkLightPreviews
import com.example.traindriver.ui.util.FieldIsFilled
import com.example.traindriver.ui.util.FontScalePreviews
import com.example.traindriver.ui.util.LocaleState

@Composable
fun StartElements(localeState: LocaleState) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    ) {
        Logo()
        InputDataElements(localeState)
        SkipButton()
    }
}

@Composable
fun Logo() {
    Text(
        modifier = Modifier.fillMaxWidth(),
        text = "Машинистам",
        style = Typography.h1,
        color = MaterialTheme.colors.onPrimary
    )
}

@Composable
fun InputDataElements(
    localeState: LocaleState,
    splashViewModel: SplashViewModel = viewModel()
) {
    val number = splashViewModel.number
    val allowEntry = splashViewModel.allowEntry

    Surface(
        modifier = Modifier.wrapContentSize(),
        shape = ShapeInputData.medium,
        color = MaterialTheme.colors.surface,
        elevation = dimensionResource(id = R.dimen.elevation_input_element)
    ) {
        Column(
            modifier = Modifier
                .padding(dimensionResource(id = R.dimen.padding_all_input_element))
                .fillMaxWidth(0.85f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.title_input_element),
                style = Typography.h3,
                color = MaterialTheme.colors.onBackground
            )

            PrimarySpacer()
            CustomTextField(
                placeholderText = stringResource(id = R.string.placeholder_input_number),
                data = number,
                transformation = {
                    localeState.transformedNumber(it)
                },
                maxLength = localeState.maxLength(),
                isFilledCallback = if (localeState != LocaleState.OTHER) {
                    object : FieldIsFilled {
                        override fun isFilled(isFilled: Boolean) {
                            allowEntry.value = isFilled
                        }
                    }
                } else null
            )

            SecondarySpacer()
            LoginButton(enabled = allowEntry.value)

            PrimarySpacer()
            Divider()

            PrimarySpacer()
            AlternativeEnteringMenu()
        }
    }
}

@Composable
fun AlternativeEnteringMenu() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircleButton(
            R.drawable.ic_launcher_foreground, stringResource(id = R.string.cont_desc_google_button)
        ) {
            Log.d("Debug", "click button login with Google")
        }
        CircleButton(
            R.drawable.ic_launcher_foreground, stringResource(id = R.string.cont_desc_email_button)
        ) {
            Log.d("Debug", "click button login by Email")
        }
        CircleButton(
            R.drawable.ic_launcher_foreground, stringResource(id = R.string.cont_desc_vk_button)
        ) {
            Log.d("Debug", "click button login by Email")
        }
    }
}

@Composable
fun Divider() {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Divider(
            modifier = Modifier
                .weight(1f)
                .padding(end = dimensionResource(id = R.dimen.padding_divider)),
            color = MaterialTheme.colors.onSurface
        )
        Text(
            modifier = Modifier.wrapContentWidth(),
            textAlign = TextAlign.Justify,
            text = stringResource(id = R.string.divider_text),
            style = Typography.body2,
            color = MaterialTheme.colors.onSurface
        )
        Divider(
            modifier = Modifier
                .weight(1f)
                .padding(start = dimensionResource(id = R.dimen.padding_divider)),
            color = MaterialTheme.colors.onSurface
        )

    }
}

@Composable
fun PrimarySpacer() {
    Spacer(
        modifier = Modifier.padding(dimensionResource(id = R.dimen.primary_padding_between_view))
    )
}

@Composable
fun SecondarySpacer() {
    Spacer(
        modifier = Modifier.padding(dimensionResource(id = R.dimen.secondary_padding_between_view))
    )
}

@Composable
fun LoginButton(enabled: Boolean) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .height(dimensionResource(id = R.dimen.min_size_view)),
        onClick = {
            /*TODO*/
        },
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colors.secondaryVariant,
            disabledBackgroundColor = MaterialTheme.colors.secondary
        ),
        enabled = enabled
    ) {
        Text(
            text = stringResource(id = R.string.text_entrance_button),
            color = MaterialTheme.colors.onSecondary,
            style = Typography.button
        )
    }
}

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
fun SkipButton(viewModel: SignInViewModel = viewModel()) {
    Text(
        modifier = Modifier.clickable(onClick = {
            viewModel.signIn(method = SignInMethod.Anonymous)
        }),
        text = stringResource(id = R.string.text_skip_button),
        style = Typography.body2,
        color = MaterialTheme.colors.onBackground
    )
}

@Composable
@DarkLightPreviews
@FontScalePreviews
private fun StartScreenPrev() {
    TrainDriverTheme {
        StartElements(localeState = LocaleState.RU)
    }
}
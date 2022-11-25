package com.example.traindriver.ui.screen.password_conf_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.traindriver.R
import com.example.traindriver.ui.screen.ScreenEnum
import com.example.traindriver.ui.screen.signin_screen.SignInViewModel
import com.example.traindriver.ui.theme.BackgroundIcon
import com.example.traindriver.ui.theme.TrainDriverTheme
import com.example.traindriver.ui.theme.Typography
import com.example.traindriver.ui.util.DarkLightPreviews
import com.example.traindriver.ui.util.FontScalePreviews

@Composable
fun PasswordConfScreen(
    navController: NavController,
    signInViewModel: SignInViewModel
) {
    val enabledResentText by remember {
        mutableStateOf(false)
    }
    val number by signInViewModel.number
    val countdown by remember {
        mutableStateOf(60)
    }
    TrainDriverTheme {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
        ) {
            val (titleText, editText, button, secondaryText, closeButton, resentText) = createRefs()
            val topGuideLine = createGuidelineFromTop(0.17f)

            Button(modifier = Modifier
                .padding(dimensionResource(id = R.dimen.medium_padding))
                .size(dimensionResource(id = R.dimen.min_size_view))
                .constrainAs(closeButton) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                },
                shape = CircleShape,
                elevation = null,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = BackgroundIcon
                ),
                onClick = {
                    navController.apply {
                        popBackStack(ScreenEnum.PASSWORD_CONFIRMATION.name, true)
                        navigate(ScreenEnum.SIGN_IN.name)
                    }
                }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_close_24),
                    contentDescription = "close",
                    contentScale = ContentScale.Crop
                )
            }

            Text(
                modifier = Modifier.constrainAs(titleText) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(topGuideLine)
                },
                style = Typography.h3,
                color = MaterialTheme.colors.onBackground,
                text = stringResource(id = R.string.title_passwordConfScreen)
            )
            PasswordEditText(modifier = Modifier
                .constrainAs(editText) {
                    top.linkTo(titleText.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .padding(top = dimensionResource(id = R.dimen.large_padding)))
            Text(
                modifier = Modifier
                    .constrainAs(secondaryText) {
                        top.linkTo(editText.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .padding(top = dimensionResource(id = R.dimen.medium_padding))
                    .wrapContentHeight(),
                text = stringResource(id = R.string.info_text_passwordConfScreen, number),
                style = Typography.body2,
                color = MaterialTheme.colors.onBackground,
                textAlign = TextAlign.Center
            )
            Button(
                modifier = Modifier
                    .constrainAs(button) {
                        top.linkTo(secondaryText.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .padding(top = dimensionResource(id = R.dimen.medium_padding)),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.secondaryVariant,
                    disabledBackgroundColor = MaterialTheme.colors.secondary
                ),
                onClick = { /*TODO*/ }) {
                Text(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    text = stringResource(id = R.string.button_text_confirm)
                )
            }

            Text(
                modifier = Modifier
                    .constrainAs(resentText) {
                        top.linkTo(button.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .clickable(
                        enabled = enabledResentText
                    ) {
                        /*TODO*/
                    }
                    .padding(top = dimensionResource(id = R.dimen.medium_padding)),
                text = stringResource(id = R.string.resent_text_passwordConfScreen, countdown),
                style = Typography.body2,
                color = MaterialTheme.colors.onBackground,
                textAlign = TextAlign.Center
            )

        }
    }
}

@Composable
@DarkLightPreviews
@FontScalePreviews
fun DefaultPreview() {
    TrainDriverTheme {
        PasswordConfScreen(rememberNavController(), viewModel())
    }
}
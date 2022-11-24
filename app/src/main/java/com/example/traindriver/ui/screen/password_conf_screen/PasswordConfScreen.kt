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
    signInViewModel: SignInViewModel = viewModel()
) {
    val enabledResentText by remember {
        mutableStateOf(false)
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
                .padding(16.dp)
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
                text = "Введите код из SMS"
            )
            PasswordEditText(modifier = Modifier
                .constrainAs(editText) {
                    top.linkTo(titleText.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .padding(top = 28.dp))
            Text(
                modifier = Modifier
                    .constrainAs(secondaryText) {
                        top.linkTo(editText.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .padding(16.dp)
                    .wrapContentHeight(),
                text = "Мы отправили SMS с кодом на номер ${signInViewModel.number.value}",
                style = Typography.body2,
                color = MaterialTheme.colors.onBackground,
            )
            Button(modifier = Modifier.constrainAs(button) {
                top.linkTo(secondaryText.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }, colors = ButtonDefaults.buttonColors(
                backgroundColor = MaterialTheme.colors.secondaryVariant,
                disabledBackgroundColor = MaterialTheme.colors.secondary
            ), onClick = { /*TODO*/ }) {
                Text(
                    modifier = Modifier.padding(horizontal = 24.dp), text = "Подтвердить"
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
                    .padding(16.dp),
                text = "Отправить повторно через 60 с",
                style = Typography.body2,
                color = MaterialTheme.colors.onBackground,
            )

        }
    }
}

@Composable
@DarkLightPreviews
@FontScalePreviews
fun DefaultPreview() {
    TrainDriverTheme {
        PasswordConfScreen(rememberNavController())
    }
}
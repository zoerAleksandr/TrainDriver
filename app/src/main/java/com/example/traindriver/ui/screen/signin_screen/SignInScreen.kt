package com.example.traindriver.ui.screen.signin_screen

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.traindriver.R
import com.example.traindriver.data.util.ResultState
import com.example.traindriver.ui.element_screen.NumberPhoneTextField
import com.example.traindriver.ui.screen.ScreenEnum
import com.example.traindriver.ui.screen.signin_screen.elements.*
import com.example.traindriver.ui.theme.ShapeInputData
import com.example.traindriver.ui.theme.TrainDriverTheme
import com.example.traindriver.ui.theme.Typography
import com.example.traindriver.ui.util.DarkLightPreviews
import com.example.traindriver.ui.util.FieldIsFilled
import com.example.traindriver.ui.util.FontScalePreviews
import com.example.traindriver.ui.util.LocaleUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun SignInScreen(
    navController: NavController,
    activity: Activity,
    signInViewModel: SignInViewModel = viewModel(),
) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val number = signInViewModel.number
    val allowEntry = signInViewModel.allowEntry
    val localeState = signInViewModel.locale

    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
        scaffoldState = scaffoldState,
        snackbarHost = {
            SnackbarHost(hostState = it) { snackBarData ->
                Snackbar(
                    snackbarData = snackBarData,
                    backgroundColor = MaterialTheme.colors.background
                )
            }
        }
    ) {
        ConstraintLayout(
            modifier = Modifier.fillMaxSize()
        ) {
            val (background, logo, inputBlock, skipButton) = createRefs()

            Background(
                modifier = Modifier
                    .constrainAs(background) { parent }
                    .fillMaxSize()
                    .background(MaterialTheme.colors.background)
            )
            Logo(
                modifier = Modifier
                    .constrainAs(logo) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(parent.top, margin = 40.dp)
                    }
                    .fillMaxWidth()
            )
            Surface(
                modifier = Modifier
                    .constrainAs(inputBlock) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }
                    .wrapContentSize(),
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
                    NumberPhoneTextField(
                        placeholderText = stringResource(id = R.string.placeholder_input_number),
                        numberState = number,
                        localeUser = localeState,
                        allowEntry = allowEntry,
                        isFilledCallback = if (localeState.value != LocaleUser.OTHER) {
                            object : FieldIsFilled {
                                override fun isFilled(isFilled: Boolean) {
                                    allowEntry.value = isFilled
                                }
                            }
                        } else null
                    )

                    SecondarySpacer()
                    LoginButton(
                        enabled = allowEntry.value,
                        onClick = {
                            scope.launch(Dispatchers.Main) {
                                signInViewModel.signInWithPhone(activity)
                                    .collect {
                                        when (it) {
                                            is ResultState.Loading -> {
                                                isLoading = true
                                                navController.navigate(ScreenEnum.PASSWORD_CONFIRMATION.name)
                                            }
                                            is ResultState.Success -> {
                                                isLoading = false
                                                Log.d("ZZZ", "Открыть экраен ввода СМС Кода")
                                            }
                                            is ResultState.Failure -> {
                                                isLoading = false
                                                scaffoldState.snackbarHostState.showSnackbar(it.msg.message.toString())
                                            }
                                        }
                                    }
                            }
                        }
                    )

                    PrimarySpacer()
                    DividerSignInScreen()

                    PrimarySpacer()
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircleButton(
                            R.drawable.ic_launcher_foreground,
                            stringResource(id = R.string.cont_desc_google_button)
                        ) {
                            Log.d("Debug", "click button login with Google")
                        }
                        CircleButton(
                            R.drawable.ic_launcher_foreground,
                            stringResource(id = R.string.cont_desc_email_button)
                        ) {
                            Log.d("Debug", "click button login by Email")
                        }
                        CircleButton(
                            R.drawable.ic_launcher_foreground,
                            stringResource(id = R.string.cont_desc_vk_button)
                        ) {
                            Log.d("Debug", "click button login by Email")
                        }
                    }
                }
            }

            SkipButton(
                modifier = Modifier
                    .constrainAs(skipButton) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom, margin = 70.dp)
                    }
                    .clickable(
                        onClick = {
                            signInViewModel.signInAnonymous()
                            navController.apply {
                                this.popBackStack(ScreenEnum.SIGN_IN.name, true)
                                this.navigate(ScreenEnum.MAIN.name)
                            }
                        }
                    )
            )
        }
    }

    if (isLoading) {
        LoadingElement()
    }
}

@Composable
@DarkLightPreviews
@FontScalePreviews
fun DefaultPreview() {
    TrainDriverTheme {
//        SignInScreen(navController = , activity = )
    }
}
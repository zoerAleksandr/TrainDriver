package com.example.traindriver.ui.screen.password_conf_screen

import android.app.Activity
import android.view.KeyEvent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.traindriver.R
import com.example.traindriver.data.util.ResultState
import com.example.traindriver.ui.element_screen.TopSnackbar
import com.example.traindriver.ui.screen.Screen
import com.example.traindriver.ui.screen.signin_screen.ResendSmsCodeResponse
import com.example.traindriver.ui.screen.signin_screen.SignInViewModel
import com.example.traindriver.ui.screen.signin_screen.WithPhoneResponse
import com.example.traindriver.ui.theme.*
import com.example.traindriver.ui.util.DarkLightPreviews
import com.example.traindriver.ui.util.FontScalePreviews
import com.example.traindriver.ui.util.SnackbarMessage.AUTO_LOGIN_MSG
import com.example.traindriver.ui.util.SnackbarMessage.CHECKING_CODE_MSG
import com.example.traindriver.ui.util.SnackbarMessage.CONNECTING_TO_SERVER_MSG
import com.example.traindriver.ui.util.SnackbarMessage.ERROR_TRY_AGAIN_MSG
import com.example.traindriver.ui.util.SnackbarMessage.SMS_SEND_MSG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PasswordConfScreen(
    navController: NavController,
    signInViewModel: SignInViewModel,
    activity: Activity
) {
    val scope = rememberCoroutineScope()
    val number by signInViewModel.number
    val countdown by signInViewModel.timer
    val resentTextEnable by signInViewModel.resetButtonEnable
    val scaffoldState = rememberScaffoldState()

    signInViewModel.countDownTimer.start()
    signInViewModel.resetPhoneAuthState()

    val v1 = remember { mutableStateOf("") }
    val v2 = remember { mutableStateOf("") }
    val v3 = remember { mutableStateOf("") }
    val v4 = remember { mutableStateOf("") }
    val v5 = remember { mutableStateOf("") }
    val v6 = remember { mutableStateOf("") }

    val code = listOf(
        v1.value, v2.value, v3.value, v4.value, v5.value, v6.value
    ).joinToString(separator = "")

    TrainDriverTheme {
        Scaffold(
            scaffoldState = scaffoldState,
            snackbarHost = {
                SnackbarHost(hostState = it)
                { snackBarData ->
                    TopSnackbar(snackBarData)
                }
            }
        ) {
            if (code.length == 6) {
                scope.launch(Dispatchers.Main) {
                    signInViewModel.phoneAuth.checkCode(code)
                        .collect { state ->
                            when (state) {
                                is ResultState.Loading -> {
                                    scaffoldState.snackbarHostState.showSnackbar(CHECKING_CODE_MSG)
                                }
                                is ResultState.Success -> {
                                    navController.apply {
                                        this.popBackStack(Screen.SignIn.route, true)
                                        this.navigate(Screen.Home.route)
                                    }
                                }
                                is ResultState.Failure -> {
                                    scaffoldState.snackbarHostState.showSnackbar(state.msg.message.toString())
                                }
                            }
                        }
                }
            }

            ConstraintLayout(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.background)
            ) {
                val (titleText, editText, secondaryText, closeButton, resentText) = createRefs()
                val topGuideLine = createGuidelineFromTop(0.17f)

                Button(modifier = Modifier
                    .padding(
                        top = dimensionResource(id = R.dimen.large_padding),
                        start = dimensionResource(id = R.dimen.medium_padding)
                    )
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
                            popBackStack(Screen.PasswordConfirmation.route, true)
                            navigate(Screen.SignIn.route)
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
                    color = MaterialTheme.colors.primary,
                    text = stringResource(id = R.string.title_passwordConfScreen)
                )
                Row(
                    modifier = Modifier
                        .constrainAs(editText) {
                            top.linkTo(titleText.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                        .padding(top = dimensionResource(id = R.dimen.large_padding)),
                    horizontalArrangement = Arrangement.Center
                ) {
                    val widthBox = dimensionResource(id = R.dimen.min_size_view)
                    val heightBox = dimensionResource(id = R.dimen.min_size_view) * 1.17f
                    val padding = dimensionResource(id = R.dimen.small_padding)
                    val focusManager = LocalFocusManager.current
                    val focusRequester = remember { FocusRequester() }
                    val keyboard = LocalSoftwareKeyboardController.current

                    LaunchedEffect(Unit) {
                        focusRequester.requestFocus()
                        keyboard?.show()
                    }
                    val customTextSelectionColors = TextSelectionColors(
                        handleColor = Color.Transparent,
                        backgroundColor = Color.Transparent,
                    )
                    CompositionLocalProvider(
                        LocalTextSelectionColors provides customTextSelectionColors
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(end = padding)
                                .width(widthBox)
                                .height(heightBox)
                                .border(
                                    color = MaterialTheme.colors.onBackground,
                                    width = dimensionResource(id = R.dimen.width_border_input_field),
                                    shape = ShapeBackground.small
                                )
                                .background(
                                    color = BackgroundIcon, shape = RoundedCornerShape(5.dp)
                                ), contentAlignment = Alignment.Center
                        ) {
                            BasicTextField(
                                modifier = Modifier
                                    .focusRequester(focusRequester)
                                    .onFocusEvent {
                                        if (it.hasFocus && v1.value.isNotEmpty()) {
                                            focusManager.moveFocus(FocusDirection.Next)
                                        }
                                    },
                                value = v1.value,
                                onValueChange = {
                                    v1.value = it
                                    if (it.length == 1) {
                                        focusManager.moveFocus(FocusDirection.Next)
                                    }
                                },
                                textStyle = Typography.h2.copy(color = MaterialTheme.colors.primary),
                                cursorBrush = SolidColor(Color.Unspecified),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number
                                )
                            )
                        }
                        Box(
                            modifier = Modifier
                                .padding(end = padding)
                                .width(widthBox)
                                .height(heightBox)
                                .border(
                                    color = MaterialTheme.colors.onBackground,
                                    width = dimensionResource(id = R.dimen.width_border_input_field),
                                    shape = ShapeBackground.small
                                )
                                .background(
                                    color = BackgroundIcon, shape = RoundedCornerShape(5.dp)
                                ), contentAlignment = Alignment.Center
                        ) {
                            BasicTextField(modifier = Modifier
                                .onKeyEvent {
                                    when (it.nativeKeyEvent.keyCode) {
                                        KeyEvent.KEYCODE_DEL -> {
                                            if (v2.value.isEmpty()) {
                                                v1.value = ""
                                            }
                                            focusManager.moveFocus(FocusDirection.Left)
                                            true
                                        }
                                        else -> false
                                    }
                                }
                                .onFocusEvent {
                                    if (it.hasFocus && v1.value.isEmpty()) {
                                        focusManager.moveFocus(FocusDirection.Left)
                                    }
                                    if (it.hasFocus && v2.value.isNotEmpty()) {
                                        focusManager.moveFocus(FocusDirection.Next)
                                    }
                                },
                                value = v2.value,
                                onValueChange = {
                                    v2.value = it
                                    if (it.length == 1) {
                                        focusManager.moveFocus(FocusDirection.Next)
                                    }
                                },
                                textStyle = Typography.h2.copy(color = MaterialTheme.colors.primary),
                                cursorBrush = SolidColor(Color.Unspecified),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number
                                )
                            )
                        }
                        Box(
                            modifier = Modifier
                                .padding(end = padding)
                                .width(widthBox)
                                .height(heightBox)
                                .border(
                                    color = MaterialTheme.colors.onBackground,
                                    width = dimensionResource(id = R.dimen.width_border_input_field),
                                    shape = ShapeBackground.small
                                )
                                .background(
                                    color = BackgroundIcon, shape = RoundedCornerShape(5.dp)
                                ), contentAlignment = Alignment.Center
                        ) {
                            BasicTextField(modifier = Modifier
                                .onKeyEvent {
                                    when (it.nativeKeyEvent.keyCode) {
                                        KeyEvent.KEYCODE_DEL -> {
                                            if (v3.value.isEmpty()) {
                                                v2.value = ""
                                            }
                                            focusManager.moveFocus(FocusDirection.Left)
                                            true
                                        }
                                        else -> false
                                    }
                                }
                                .onFocusEvent {
                                    if (it.hasFocus && v2.value.isEmpty()) {
                                        focusManager.moveFocus(FocusDirection.Left)
                                    }
                                    if (it.hasFocus && v3.value.isNotEmpty()) {
                                        focusManager.moveFocus(FocusDirection.Next)
                                    }
                                },
                                value = v3.value,
                                onValueChange = {
                                    v3.value = it
                                    if (it.length == 1) {
                                        focusManager.moveFocus(FocusDirection.Next)
                                    }
                                },
                                textStyle = Typography.h2.copy(color = MaterialTheme.colors.primary),
                                cursorBrush = SolidColor(Color.Unspecified),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number
                                )
                            )
                        }
                        Box(
                            modifier = Modifier
                                .padding(end = padding)
                                .width(widthBox)
                                .height(heightBox)
                                .border(
                                    color = MaterialTheme.colors.onBackground,
                                    width = dimensionResource(id = R.dimen.width_border_input_field),
                                    shape = ShapeBackground.small
                                )
                                .background(
                                    color = BackgroundIcon, shape = RoundedCornerShape(5.dp)
                                ), contentAlignment = Alignment.Center
                        ) {
                            BasicTextField(modifier = Modifier
                                .onKeyEvent {
                                    when (it.nativeKeyEvent.keyCode) {
                                        KeyEvent.KEYCODE_DEL -> {
                                            if (v4.value.isEmpty()) {
                                                v3.value = ""
                                            }
                                            focusManager.moveFocus(FocusDirection.Left)
                                            true
                                        }
                                        else -> false
                                    }
                                }
                                .onFocusEvent {
                                    if (it.hasFocus && v3.value.isEmpty()) {
                                        focusManager.moveFocus(FocusDirection.Left)
                                    }
                                    if (it.hasFocus && v4.value.isNotEmpty()) {
                                        focusManager.moveFocus(FocusDirection.Next)
                                    }
                                },
                                value = v4.value,
                                onValueChange = {
                                    v4.value = it
                                    if (it.length == 1) {
                                        focusManager.moveFocus(FocusDirection.Next)
                                    }
                                },
                                textStyle = Typography.h2.copy(color = MaterialTheme.colors.primary),
                                cursorBrush = SolidColor(Color.Unspecified),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number
                                )
                            )
                        }
                        Box(
                            modifier = Modifier
                                .padding(end = padding)
                                .width(widthBox)
                                .height(heightBox)
                                .border(
                                    color = MaterialTheme.colors.onBackground,
                                    width = dimensionResource(id = R.dimen.width_border_input_field),
                                    shape = ShapeBackground.small
                                )
                                .background(
                                    color = BackgroundIcon, shape = RoundedCornerShape(5.dp)
                                ), contentAlignment = Alignment.Center
                        ) {
                            BasicTextField(modifier = Modifier
                                .onKeyEvent {
                                    when (it.nativeKeyEvent.keyCode) {
                                        KeyEvent.KEYCODE_DEL -> {
                                            if (v5.value.isEmpty()) {
                                                v4.value = ""
                                            }
                                            focusManager.moveFocus(FocusDirection.Left)
                                            true
                                        }
                                        else -> false
                                    }
                                }
                                .onFocusEvent {
                                    if (it.hasFocus && v4.value.isEmpty()) {
                                        focusManager.moveFocus(FocusDirection.Left)
                                    }
                                    if (it.hasFocus && v5.value.isNotEmpty()) {
                                        focusManager.moveFocus(FocusDirection.Next)
                                    }
                                },
                                value = v5.value,
                                onValueChange = {
                                    v5.value = it
                                    if (it.length == 1) {
                                        focusManager.moveFocus(FocusDirection.Next)
                                    }
                                },
                                textStyle = Typography.h2.copy(color = MaterialTheme.colors.primary),
                                cursorBrush = SolidColor(Color.Unspecified),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number
                                )
                            )
                        }
                        Box(
                            modifier = Modifier
                                .width(widthBox)
                                .height(heightBox)
                                .border(
                                    color = MaterialTheme.colors.onBackground,
                                    width = dimensionResource(id = R.dimen.width_border_input_field),
                                    shape = ShapeBackground.small
                                )
                                .background(
                                    color = BackgroundIcon, shape = RoundedCornerShape(5.dp)
                                ), contentAlignment = Alignment.Center
                        ) {
                            BasicTextField(
                                modifier = Modifier.onFocusEvent {
                                    if (it.hasFocus && v5.value.isEmpty()) {
                                        focusManager.moveFocus(FocusDirection.Left)
                                    }
                                },
                                value = v6.value,
                                onValueChange = {
                                    if (it.length <= 1) {
                                        v6.value = it
                                    }
                                },
                                textStyle = Typography.h2.copy(color = MaterialTheme.colors.primary),
                                cursorBrush = SolidColor(Color.Unspecified),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Number
                                )
                            )
                        }
                    }
                }

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

                TextButton(
                    modifier = Modifier
                        .constrainAs(resentText) {
                            top.linkTo(secondaryText.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                        .padding(top = 5.dp),
                    enabled = resentTextEnable,
                    onClick = {
                        signInViewModel.resetButtonEnable.value = false
                        signInViewModel.countDownTimer.start()
                        signInViewModel.phoneAuth.resendCode(activity)
                    }
                ) {
                    Text(
                        text = if (resentTextEnable) {
                            stringResource(id = R.string.resent_text_passwordConfScreen_enabled)
                        } else {
                            stringResource(id = R.string.resent_text_passwordConfScreen, countdown)
                        },
                        style = if (resentTextEnable) {
                            Typography.body1
                        } else {
                            Typography.body2
                        },
                        color = if (resentTextEnable) {
                            ClicableTextColor
                        } else {
                            MaterialTheme.colors.primary
                        },
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }

    ResendCode(
        resendCode = signInViewModel.resendSmsCodeResponse,
        snackbarHostState = scaffoldState.snackbarHostState
    )
}

@Composable
fun ResendCode(
    resendCode: ResendSmsCodeResponse,
    snackbarHostState: SnackbarHostState
) {
    val scope = rememberCoroutineScope()

    when (resendCode) {
        is ResultState.Loading -> {
            scope.launch {
                snackbarHostState.showSnackbar(CONNECTING_TO_SERVER_MSG)
            }
        }
        is ResultState.Success -> resendCode.data?.let { result ->
            scope.launch {
                when (result) {
                    is WithPhoneResponse.SmsSend -> {
                        snackbarHostState.showSnackbar(SMS_SEND_MSG)
                    }
                    is WithPhoneResponse.AutoSignIn -> {
                        snackbarHostState.showSnackbar(AUTO_LOGIN_MSG)
                    }
                }
            }
        }
        is ResultState.Failure -> {
            scope.launch {
                snackbarHostState.showSnackbar(ERROR_TRY_AGAIN_MSG)
            }
        }
    }
}

@Composable
@DarkLightPreviews
@FontScalePreviews
fun DefaultPreview() {
    TrainDriverTheme {
        PasswordConfScreen(rememberNavController(), viewModel(), Activity())
    }
}
package com.example.traindriver.ui.screen.signin_screen

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.traindriver.R
import com.example.traindriver.ui.element_screen.HandleBottomSheet
import com.example.traindriver.ui.element_screen.NumberPhoneTextField
import com.example.traindriver.ui.element_screen.TopSnackbar
import com.example.traindriver.ui.element_screen.getAllLocaleExcept
import com.example.traindriver.ui.screen.ScreenEnum
import com.example.traindriver.ui.screen.signin_screen.components.CreateUserWithPhone
import com.example.traindriver.ui.screen.signin_screen.components.OneTapSignIn
import com.example.traindriver.ui.screen.signin_screen.components.SignInWithGoogle
import com.example.traindriver.ui.screen.signin_screen.elements.*
import com.example.traindriver.ui.theme.ShapeButton
import com.example.traindriver.ui.theme.ShapeSurface
import com.example.traindriver.ui.theme.TrainDriverTheme
import com.example.traindriver.ui.theme.Typography
import com.example.traindriver.ui.util.DarkLightPreviews
import com.example.traindriver.ui.util.FieldIsFilled
import com.example.traindriver.ui.util.FontScalePreviews
import com.example.traindriver.ui.util.LocaleUser
import com.example.traindriver.ui.util.SnackbarMessage.ERROR_TRY_AGAIN_MSG
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SignInScreen(
    navController: NavController,
    activity: Activity,
    viewModel: SignInViewModel,
) {
    val secondaryPadding = dimensionResource(R.dimen.secondary_padding_between_view)
    val primaryPadding = dimensionResource(R.dimen.primary_padding_between_view)
    val loadingState = remember { mutableStateOf(false) }

    val scaffoldState = rememberScaffoldState()
    val sheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)

    val scope = rememberCoroutineScope()

    val number = viewModel.number
    val allowEntry = viewModel.allowEntry
    val localeState = viewModel.locale

    if (localeState.value == LocaleUser.OTHER) {
        scope.launch {
            sheetState.show()
        }
    } else {
        scope.launch {
            sheetState.hide()
        }
    }

    ModalBottomSheetLayout(sheetState = sheetState,
        sheetShape = ShapeSurface.medium,
        sheetBackgroundColor = MaterialTheme.colors.background,
        sheetContent = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                HandleBottomSheet()
                Text(
                    modifier = Modifier
                        .padding(start = primaryPadding, top = primaryPadding)
                        .align(Alignment.Start),
                    color = MaterialTheme.colors.primary,
                    text = stringResource(id = R.string.select_country),
                    style = Typography.h4
                )
                LazyColumn(
                    modifier = Modifier.padding(start = primaryPadding, top = secondaryPadding)
                ) {
                    items(getAllLocaleExcept(localeState.value)) { locale ->
                        BottomSheetLocaleListItem(locale = locale, onItemClick = { currentLocale ->
                            localeState.value = currentLocale
                            number.value = currentLocale.prefix()
                            allowEntry.value = (currentLocale == LocaleUser.OTHER)
                            scope.launch {
                                sheetState.hide()
                            }
                        })
                    }
                }
                PrimarySpacer()
            }
        }) {
        Scaffold(scaffoldState = scaffoldState, snackbarHost = {
            SnackbarHost(
                hostState = it
            ) { snackBarData ->
                TopSnackbar(snackBarData)
            }
        }) {
            ConstraintLayout(
                modifier = Modifier.fillMaxSize()
            ) {
                val (background, logo, inputBlock, skipButton) = createRefs()

                Background(modifier = Modifier.constrainAs(background) { parent })
                Logo(modifier = Modifier
                    .constrainAs(logo) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(parent.top)
                        bottom.linkTo(inputBlock.top)
                    }
                    .fillMaxWidth())
                Surface(modifier = Modifier
                    .constrainAs(inputBlock) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }
                    .wrapContentSize(),
                    shape = ShapeSurface.medium,
                    color = MaterialTheme.colors.background,
                    elevation = dimensionResource(id = R.dimen.elevation_input_element)) {
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
                            color = MaterialTheme.colors.primary
                        )

                        PrimarySpacer()
                        NumberPhoneTextField(
                            numberState = number,
                            localeUser = localeState,
                            isFilledCallback = if (localeState.value != LocaleUser.OTHER) {
                                object : FieldIsFilled {
                                    override fun isFilled(isFilled: Boolean) {
                                        allowEntry.value = isFilled
                                    }
                                }
                            } else null,
                            sheetState = sheetState) {
                            viewModel.phoneAuth.createUserWithPhone(activity)
                        }

                        SecondarySpacer()
                        LoginButton(enabled = allowEntry.value, isLoading = loadingState.value) {
                            viewModel.phoneAuth.createUserWithPhone(activity)
                        }

                        SecondarySpacer()
                        DividerSignInScreen()

                        SecondarySpacer()
                        Button(modifier = Modifier.fillMaxWidth(),
                            shape = ShapeButton.medium,
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = MaterialTheme.colors.surface
                            ),
                            onClick = {
                                viewModel.authWithGoogle.oneTap()
                            }) {
                            Text(
                                text = stringResource(id = R.string.text_entrance_with_google),
                                color = MaterialTheme.colors.primary
                            )
                        }
                    }
                }

                SkipButton(modifier = Modifier
                    .constrainAs(skipButton) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                        top.linkTo(inputBlock.bottom)
                    }
                    .clickable(onClick = {
                        viewModel.anonymousAuth.signIn(activity as LifecycleOwner)
                        navigateToMainScreen(navController)
                    })
                )
            }
        }
    }

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                try {
                    val credentials =
                        viewModel.oneTapClient.getSignInCredentialFromIntent(result.data)
                    val googleIdToken = credentials.googleIdToken
                    val googleCredentials = GoogleAuthProvider.getCredential(googleIdToken, null)
                    viewModel.authWithGoogle.signIn(googleCredentials)
                } catch (it: ApiException) {
                    scope.launch {
                        scaffoldState.snackbarHostState.showSnackbar(ERROR_TRY_AGAIN_MSG)
                    }
                }
            }
        }

    fun launch(signInResult: BeginSignInResult) {
        val intent = IntentSenderRequest.Builder(signInResult.pendingIntent.intentSender).build()
        launcher.launch(intent)
    }

    OneTapSignIn(
        oneTapResponse = viewModel.oneTapSignInResponse,
        snackbarHostState = scaffoldState.snackbarHostState,
        launch = {
            launch(it)
        }
    )

    SignInWithGoogle(
        signInWithGoogleResponse = viewModel.signInWithGoogleResponse,
        snackbarHostState = scaffoldState.snackbarHostState,
        navigateToMainScreen = { signedIn ->
            if (signedIn) {
                navigateToMainScreen(navController)
            }
        }
    )

    CreateUserWithPhone(
        createUserWithPhone = viewModel.createUserWithPhoneResponse,
        snackbarHostState = scaffoldState.snackbarHostState,
        navController = navController
    )
}

private fun navigateToMainScreen(navController: NavController) {
    navController.apply {
        this.popBackStack(ScreenEnum.SIGN_IN.name, true)
        this.navigate(ScreenEnum.MAIN.name)
    }
}

@Composable
@DarkLightPreviews
@FontScalePreviews
fun DefaultPreview() {
    TrainDriverTheme {
        SignInScreen(rememberNavController(), Activity(), viewModel())
    }
}
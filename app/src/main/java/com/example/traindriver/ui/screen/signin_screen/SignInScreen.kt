package com.example.traindriver.ui.screen.signin_screen

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.traindriver.R
import com.example.traindriver.data.util.ResultState
import com.example.traindriver.ui.element_screen.NumberPhoneTextField
import com.example.traindriver.ui.element_screen.TopSnackbar
import com.example.traindriver.ui.element_screen.getAllLocaleExcept
import com.example.traindriver.ui.screen.ScreenEnum
import com.example.traindriver.ui.screen.signin_screen.elements.*
import com.example.traindriver.ui.theme.*
import com.example.traindriver.ui.util.DarkLightPreviews
import com.example.traindriver.ui.util.FieldIsFilled
import com.example.traindriver.ui.util.FontScalePreviews
import com.example.traindriver.ui.util.LocaleUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SignInScreen(
    navController: NavController,
    activity: Activity,
    signInViewModel: SignInViewModel,
) {
    val loadingState = remember { mutableStateOf(false) }

    val scaffoldState = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()
    val number = signInViewModel.number
    val allowEntry = signInViewModel.allowEntry
    val localeState = signInViewModel.locale

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        snackbarHost = {
            SnackbarHost(
                hostState = it
            ) { snackBarData ->
                TopSnackbar(snackBarData)
            }
        },
        sheetShape = RoundedCornerShape(30.dp),
        sheetBackgroundColor = BackgroundBottomSheet,
        sheetElevation = 25.dp,
        sheetContent = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Divider(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .size(width = 50.dp, height = 4.dp),
                )
                Text(
                    modifier = Modifier
                        .padding(25.dp)
                        .align(Alignment.Start),
                    color = MaterialTheme.colors.primary,
                    text = "Выберите страну",
                    style = Typography.h4
                )
                LazyColumn(
                    modifier = Modifier
                        .padding(start = 25.dp)
                ) {
                    items(getAllLocaleExcept(localeState.value)) { locale ->
                        BottomSheetLocaleListItem(
                            locale = locale,
                            onItemClick = { currentLocale ->
                                localeState.value = currentLocale
                                number.value = currentLocale.prefix()
                                allowEntry.value = (currentLocale == LocaleUser.OTHER)
                                scope.launch {
                                    scaffoldState.bottomSheetState.collapse()
                                }
                            }
                        )
                    }
                }
                PrimarySpacer()
            }
        },
        sheetPeekHeight = 0.dp
    ) {
        ConstraintLayout(
            modifier = Modifier.fillMaxSize()
        ) {
            val (background, logo, inputBlock, skipButton) = createRefs()

            Background(
                modifier = Modifier
                    .constrainAs(background) { parent }
            )
            Logo(modifier = Modifier
                .constrainAs(logo) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top, margin = 40.dp)
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
                shape = ShapeInputData.medium,
                color = MaterialTheme.colors.background,
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
                        color = MaterialTheme.colors.primary
                    )

                    PrimarySpacer()
                    NumberPhoneTextField(
                        numberState = number,
                        localeUser = localeState,
                        allowEntry = allowEntry,
                        isFilledCallback = if (localeState.value != LocaleUser.OTHER) {
                            object : FieldIsFilled {
                                override fun isFilled(isFilled: Boolean) {
                                    allowEntry.value = isFilled
                                }
                            }
                        } else null,
                        bottomSheetState = scaffoldState.bottomSheetState
                    )

                    SecondarySpacer()
                    LoginButton(
                        enabled = allowEntry.value, isLoading = loadingState.value
                    ) {
                        scope.launch(Dispatchers.Main) {
                            signInViewModel.phoneAuth.createUserWithPhone(activity).collect {
                                when (it) {
                                    is ResultState.Loading -> {
                                        loadingState.value = true
                                        allowEntry.value = false
                                    }
                                    is ResultState.Success -> {
                                        loadingState.value = false
                                        allowEntry.value = true
                                        navController.navigate(ScreenEnum.PASSWORD_CONFIRMATION.name)
                                    }
                                    is ResultState.Failure -> {
                                        loadingState.value = false
                                        allowEntry.value = true
                                        scaffoldState.snackbarHostState.showSnackbar(it.msg.message.toString())
                                    }
                                }
                            }
                        }
                    }

                    SecondarySpacer()
                    DividerSignInScreen()

                    SecondarySpacer()
                    Button(
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = ShapeButton.medium,
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.surface
                        ),
                        onClick = { /*TODO*/ }
                    ) {
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
                    bottom.linkTo(parent.bottom, margin = 70.dp)
                }
                .clickable(onClick = {
                    signInViewModel.anonymousAuth.signIn()
                    navController.apply {
                        this.popBackStack(ScreenEnum.SIGN_IN.name, true)
                        this.navigate(ScreenEnum.MAIN.name)
                    }
                })
            )
        }
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
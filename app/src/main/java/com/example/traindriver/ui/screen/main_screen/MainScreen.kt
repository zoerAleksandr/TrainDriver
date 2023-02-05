package com.example.traindriver.ui.screen.main_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.traindriver.R
import com.example.traindriver.ui.element_screen.HandleBottomSheet
import com.example.traindriver.ui.element_screen.TopSnackbar
import com.example.traindriver.ui.screen.main_screen.elements.CircularIndicator
import com.example.traindriver.ui.theme.*
import com.example.traindriver.ui.util.DarkLightPreviews
import com.example.traindriver.ui.util.changeAlphaWithScroll

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MainScreen(
    navController: NavController, mainViewModel: MainViewModel = viewModel()
) {
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState()
    val scaffoldState = rememberScaffoldState()
    val offset = bottomSheetScaffoldState.bottomSheetState.offset

    BottomSheetScaffold(sheetBackgroundColor = changeAlphaWithScroll(
        offset = offset.value, initColor = MaterialTheme.colors.surface
    ),
        scaffoldState = bottomSheetScaffoldState,
        sheetElevation = 0.dp,
        sheetPeekHeight = 260.dp,
        sheetShape = ShapeSurface.medium,
        sheetContent = {
            BottomSheetContent()
        }) {
        Scaffold(backgroundColor = MaterialTheme.colors.background,
            scaffoldState = scaffoldState,
            snackbarHost = {
                SnackbarHost(
                    hostState = it
                ) { snackBarData ->
                    TopSnackbar(snackBarData)
                }
            },
            topBar = {
                TopBarMainScreen()
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularIndicator(valueHour = 120)

                Button(
                    modifier = Modifier
                        .fillMaxWidth(0.75f),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = SpecialColor,
                        disabledBackgroundColor = SpecialDisableColor,
                        contentColor = MaterialTheme.colors.onSecondary
                    ),
                    shape = ShapeButton.medium,
                    onClick = { /*TODO*/ }
                ) {
                    Text(
                        text = stringResource(id = R.string.add_itinerary),
                        style = Typography.button
                    )
                }
            }
        }
    }
}

@Composable
private fun BottomSheetContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.88f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HandleBottomSheet()
        LazyColumn {
            //TODO items
        }
    }
}

@Composable
private fun TopBarMainScreen() {
    Row(
        modifier = Modifier
            .fillMaxHeight(0.12f)
            .fillMaxWidth()
            .padding(dimensionResource(id = R.dimen.medium_padding)),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        IconButton(modifier = Modifier
            .size(dimensionResource(id = R.dimen.min_size_view))
            .background(
                color = MaterialTheme.colors.surface, shape = CircleShape
            ), onClick = { /*TODO*/ }) {
            Image(
                modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size)),
                painter = painterResource(id = R.drawable.person_icon),
                contentDescription = stringResource(id = R.string.account)
            )
        }
        MonthButton(modifier = Modifier.clickable {
            // TODO
        })
        IconButton(modifier = Modifier
            .size(dimensionResource(id = R.dimen.min_size_view))
            .background(
                color = MaterialTheme.colors.surface, shape = CircleShape
            ), onClick = { /*TODO*/ }) {
            Image(
                modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size)),
                painter = painterResource(id = R.drawable.search_icon),
                contentDescription = stringResource(id = R.string.search)
            )
        }
    }
}

@Composable
fun MonthButton(modifier: Modifier) {
    Row(modifier = modifier) {
        Text(text = "ЯНВАРЬ")
        Text(text = " ")
        Text(text = "2023")
    }
}

@Composable
@DarkLightPreviews
private fun MainPreview() {
    TrainDriverTheme {
        MainScreen(navController = rememberNavController())
    }
}
package com.example.traindriver.ui.screen.main_screen

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.traindriver.R
import com.example.traindriver.ui.element_screen.TopSnackbar
import com.example.traindriver.ui.screen.Screen
import com.example.traindriver.ui.screen.main_screen.elements.BottomSheetContent
import com.example.traindriver.ui.screen.main_screen.elements.CircularIndicator
import com.example.traindriver.ui.screen.main_screen.elements.TopBarMainScreen
import com.example.traindriver.ui.theme.*
import com.example.traindriver.ui.util.DarkLightPreviews
import com.example.traindriver.ui.util.changeAlphaWithScroll
import com.example.traindriver.ui.util.long_util.getHour
import com.example.traindriver.ui.util.long_util.getRemainingMinuteFromHour

@Composable
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
fun MainScreen(
    navController: NavController, mainViewModel: MainViewModel = viewModel()
) {
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState()
    val snackbarHostState = remember { SnackbarHostState() }
    val offset = bottomSheetScaffoldState.bottomSheetState
    val totalTime = mainViewModel.totalTime
    val dd = rememberStandardBottomSheetState()

    BottomSheetScaffold(
//        sheetContainerColor = changeAlphaWithScroll(
//            offset = offset.value,
//            initColor = MaterialTheme.colorScheme.surface
//        ),
        scaffoldState = bottomSheetScaffoldState,
        sheetPeekHeight = 260.dp,
        sheetShape = ShapeSurface.medium,
        sheetContent = {
            BottomSheetContent(
                mainViewModel.listRoute,
                snackbarHostState,
                navController
            )
        }) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            snackbarHost = {
                SnackbarHost(
                    hostState = bottomSheetScaffoldState.snackbarHostState
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
                CircularIndicator(
                    valueHour = totalTime.getHour(),
                    valueMinute = totalTime.getRemainingMinuteFromHour()
                )
                Button(
                    modifier = Modifier
                        .fillMaxWidth(0.75f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SpecialColor,
                        disabledContainerColor = SpecialDisableColor,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    ),
                    shape = ShapeButton.medium,
                    onClick = {
                        navController.navigate(Screen.Adding.route)
                    }
                ) {
                    Text(
                        text = stringResource(id = R.string.add_itinerary),
                        style = Typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
@DarkLightPreviews
private fun MainPreview() {
    TrainDriverTheme {
        MainScreen(navController = rememberNavController())
    }
}
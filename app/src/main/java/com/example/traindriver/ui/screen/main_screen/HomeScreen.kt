package com.example.traindriver.ui.screen.main_screen

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.traindriver.R
import com.example.traindriver.ui.element_screen.TopSnackbar
import com.example.traindriver.ui.screen.Screen
import com.example.traindriver.ui.screen.main_screen.elements.HomeBottomSheetContent
import com.example.traindriver.ui.screen.main_screen.elements.CircularIndicator
import com.example.traindriver.ui.theme.*
import com.example.traindriver.ui.util.DarkLightPreviews
import com.example.traindriver.ui.util.OnLifecycleEvent
import com.example.traindriver.ui.util.changeAlphaWithScroll
import com.example.traindriver.ui.util.long_util.getHour
import com.example.traindriver.ui.util.long_util.getRemainingMinuteFromHour
import kotlinx.coroutines.launch
import java.util.*

@Composable
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
fun HomeScreen(
    navController: NavController, mainViewModel: MainViewModel = viewModel()
) {
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            confirmValueChange = {
                it != SheetValue.Hidden
            }
        )
    )
    val scope = rememberCoroutineScope()

    val heightScreen = LocalConfiguration.current.screenHeightDp
    val widthScreen = LocalConfiguration.current.screenWidthDp
    val sheetPeekHeight = heightScreen.times(0.3)
    val indicatorSize = widthScreen.times(0.75)
    val contentHeight = (heightScreen - 64).dp

    val offset = try {
        bottomSheetScaffoldState.bottomSheetState.requireOffset()
    } catch (e: Throwable) {
        200f
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val totalTime = mainViewModel.totalTime

    OnLifecycleEvent { _, event ->
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                val currentMonth = Calendar.getInstance().get(Calendar.MONTH)
                mainViewModel.getListItineraryByMonth(currentMonth)
                scope.launch {
                    bottomSheetScaffoldState.bottomSheetState.partialExpand()
                }
            }
            else -> {}
        }
    }

    BottomSheetScaffold(
        sheetContainerColor = changeAlphaWithScroll(
            offset = offset,
            initColor = MaterialTheme.colorScheme.background
        ),
        sheetDragHandle = {
            BottomSheetDefaults.DragHandle(
                color = changeAlphaWithScroll(
                    offset = offset,
                    initColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        sheetShadowElevation = 0.dp,
        scaffoldState = bottomSheetScaffoldState,
        sheetPeekHeight = sheetPeekHeight.dp,
        sheetContent = {
            HomeBottomSheetContent(
                mainViewModel.listRoute,
                snackbarHostState,
                navController,
                contentHeight,
                offset
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        navigationIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    title = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            /*TODO*/
                            Text(text = "ЯНВАРЬ", style = Typography.titleMedium)
                            Text(text = " ")
                            Text(text = "2023", style = Typography.titleMedium)
                        }
                    },
                    actions = {
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                        }

                    },
                    navigationIcon = {
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(imageVector = Icons.Default.Person, contentDescription = "User")
                        }
                    }
                )
            },
            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarHostState
                ) { snackBarData ->
                    TopSnackbar(snackBarData)
                }
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.7f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularIndicator(
                    canvasSize = indicatorSize.dp,
                    valueHour = totalTime.getHour(),
                    valueMinute = totalTime.getRemainingMinuteFromHour()
                )
                Button(
                    modifier = Modifier
                        .fillMaxWidth(0.70f),
                    onClick = {
                        navController.navigate(Screen.Adding.route)
                    }
                ) {
                    Text(
                        text = stringResource(id = R.string.add_itinerary),
                        style = Typography.labelLarge
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
        HomeScreen(navController = rememberNavController())
    }
}
package com.example.traindriver.ui.screen.viewing_route_screen

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.traindriver.R
import com.example.traindriver.data.util.ResultState
import com.example.traindriver.domain.entity.Route
import com.example.traindriver.ui.element_screen.CustomScrollableTabRow
import com.example.traindriver.ui.element_screen.TopSnackbar
import com.example.traindriver.ui.screen.ROUTE
import com.example.traindriver.ui.screen.Screen
import com.example.traindriver.ui.screen.signin_screen.elements.SecondarySpacer
import com.example.traindriver.ui.screen.viewing_route_screen.element.*
import com.example.traindriver.ui.theme.TrainDriverTheme
import com.example.traindriver.ui.theme.Typography
import com.example.traindriver.ui.util.DarkLightPreviews
import com.example.traindriver.ui.util.DateAndTimeFormat.DATE_FORMAT
import com.example.traindriver.ui.util.OnLifecycleEvent
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
@Composable
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
fun ViewingRouteScreen(
    navController: NavController,
    viewModel: ViewingRouteViewModel = viewModel()
) {
    val uid = navController.currentBackStackEntry?.arguments?.getString(ROUTE)
    OnLifecycleEvent { _, event ->
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                uid?.let {
                    viewModel.getRouteById(uid)
                }
            }
            else -> {}
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }

    val pagerState = rememberPagerState(
        pageCount = 5,
        initialPage = 0
    )
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { snackBarData ->
                TopSnackbar(snackBarData)
            }
        },
        topBar = {
            TopAppBar(
                title = {
                    Header(response = viewModel.routeState, snackbarHostState = snackbarHostState)
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.navigateUp() }
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        navController.navigate(
                            Screen.Adding.setId(
                                uid ?: ""
                            )
                        )
                    }) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Изменить")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    titleContentColor = MaterialTheme.colorScheme.primary,
                    navigationIconContentColor = MaterialTheme.colorScheme.primary,
                    actionIconContentColor = MaterialTheme.colorScheme.primary
                ),
                scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Tabs(pagerState)
            TabContent(pagerState, navController, viewModel)
        }
    }
}

@Composable
private fun Header(response: RouteResponse, snackbarHostState: SnackbarHostState) {
    val currentRoute = remember {
        mutableStateOf(Route())
    }
    DataHeader(route = currentRoute.value)
    when (response) {
        is ResultState.Loading -> {
        }
        is ResultState.Success -> response.data?.let { route ->
            currentRoute.value = route
        }
        is ResultState.Failure -> {
            LaunchedEffect(currentRoute) {
                snackbarHostState.showSnackbar(response.msg.message.toString())
            }
        }
    }
}

@Composable
private fun DataHeader(route: Route) {
    val dateStartWorkText = route.timeStartWork?.let { millis ->
        SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(millis)
    } ?: ""

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 32.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        route.number?.let { number ->
            Text(
                text = "№ $number",
                style = Typography.titleLarge
            )
        }
        SecondarySpacer()
        Text(
            text = dateStartWorkText,
            style = Typography.titleLarge
        )
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun Tabs(pagerState: PagerState) {
    val tabsLabel = listOf(
        stringResource(id = R.string.work_time),
        stringResource(id = R.string.locomotive),
        stringResource(id = R.string.train),
        stringResource(id = R.string.passenger),
        stringResource(id = R.string.notes)
    )
    CustomScrollableTabRow(
        tabs = tabsLabel,
        selectedTabIndex = pagerState.currentPage,
        pagerState = pagerState
    )
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun TabContent(
    pagerState: PagerState,
    navController: NavController,
    viewModel: ViewingRouteViewModel
) {
    HorizontalPager(state = pagerState) { page ->
        when (page) {
            0 -> WorkTimeScreen(navController, viewModel.routeState, viewModel.minTimeRest)
            1 -> LocoScreen(viewModel.routeState)
            2 -> TrainScreen(viewModel.routeState)
            3 -> PassengerScreen(viewModel.routeState)
            4 -> NotesScreen(viewModel.routeState, navController)
        }
    }
}

@Composable
@DarkLightPreviews
private fun ViewingPrev() {
    TrainDriverTheme {
        ViewingRouteScreen(rememberNavController(), viewModel())
    }
}
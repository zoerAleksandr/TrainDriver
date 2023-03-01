package com.example.traindriver.ui.screen.viewing_route_screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.traindriver.ui.screen.viewing_route_screen.element.*
import com.example.traindriver.ui.theme.TrainDriverTheme
import com.example.traindriver.ui.theme.Typography
import com.example.traindriver.ui.util.Constants.ROUTE
import com.example.traindriver.ui.util.DarkLightPreviews
import com.example.traindriver.ui.util.DateAndTimeFormat.DATE_FORMAT
import com.example.traindriver.ui.util.OnLifecycleEvent
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat

@Composable
fun ViewingRouteScreen(
    navController: NavController,
    viewModel: ViewingRouteViewModel = viewModel()
) {
    val scaffoldState = rememberScaffoldState()
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

    Scaffold(scaffoldState = scaffoldState, snackbarHost = {
        SnackbarHost(
            hostState = it
        ) { snackBarData ->
            TopSnackbar(snackBarData)
        }
    }) {
        TabScreen(navController, scaffoldState.snackbarHostState, viewModel)
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun TabScreen(
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    viewModel: ViewingRouteViewModel
) {
    val pagerState = rememberPagerState(
        pageCount = 4,
        initialPage = 0
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 38.dp)
    ) {
        Header(response = viewModel.routeState, snackbarHostState = snackbarHostState)
        Tabs(pagerState)
        TabContent(pagerState, navController, viewModel)
    }
}

@Composable
private fun Header(response: RouteResponse, snackbarHostState: SnackbarHostState) {
    val currentRoute = remember {
        mutableStateOf(Route())
    }
    DataHeader(route = currentRoute.value)
    val scope = rememberCoroutineScope()
    when (response) {
        is ResultState.Loading -> {
        }
        is ResultState.Success -> response.data?.let { route ->
            currentRoute.value = route
        }
        is ResultState.Failure -> {
            scope.launch {
                snackbarHostState.showSnackbar(response.msg.message.toString())
            }
        }
    }
}

@Composable
private fun DataHeader(route: Route) {
    val dateStartWorkText = route.timeStartWork?.let { millis ->
        SimpleDateFormat(DATE_FORMAT).format(millis)
    } ?: ""

    val routeNumberText = route.number ?: "0000"
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = dateStartWorkText,
            style = Typography.subtitle1.copy(color = setTextColor(route.timeStartWork))
        )
        Text(
            text = "№ $routeNumberText",
            style = Typography.subtitle1.copy(color = setTextColor(route.number))
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
            1 -> LocoScreen(viewModel.routeState, navController)
            2 -> TrainScreen(viewModel.routeState)
            3 -> PassengerScreen(viewModel.routeState)
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
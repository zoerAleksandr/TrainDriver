package com.example.traindriver.ui.screen.viewing_route_screen

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
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
import com.example.traindriver.ui.screen.viewing_route_screen.element.LocoScreen
import com.example.traindriver.ui.screen.viewing_route_screen.element.PassengerScreen
import com.example.traindriver.ui.screen.viewing_route_screen.element.TrainScreen
import com.example.traindriver.ui.screen.viewing_route_screen.element.WorkTimeScreen
import com.example.traindriver.ui.theme.TrainDriverTheme
import com.example.traindriver.ui.theme.Typography
import com.example.traindriver.ui.util.Constants.ROUTE
import com.example.traindriver.ui.util.DarkLightPreviews
import com.example.traindriver.ui.util.OnLifecycleEvent
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import java.text.SimpleDateFormat

@Composable
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

    val state = viewModel.routeState

    Scaffold {
        when (state) {
            is ResultState.Loading -> {
                Log.d("ZZZ", "Loading")
            }
            is ResultState.Success -> state.data?.let { route ->
                TabScreen(route, navController)
            }
            is ResultState.Failure -> {}
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun TabScreen(route: Route, navController: NavController) {
    val pagerState = rememberPagerState(
        pageCount = 4,
        initialPage = 0
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 38.dp)
    ) {
        Header(route = route)
        Tabs(pagerState)
        TabContent(pagerState, route, navController)
    }
}

@Composable
private fun Header(route: Route) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val millis = route.timeStartWork
        val dateFormatted = SimpleDateFormat("dd.MM.yyyy").format(millis)
        route.number?.let { number ->
            Text(text = "№ $number", style = Typography.body1)
        }
        Text(text = dateFormatted, style = Typography.body1)
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
private fun TabContent(pagerState: PagerState, route: Route, navController: NavController) {
    HorizontalPager(state = pagerState) { page ->
        when (page) {
            0 -> WorkTimeScreen(navController, route)
            1 -> LocoScreen()
            2 -> TrainScreen()
            3 -> PassengerScreen()
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
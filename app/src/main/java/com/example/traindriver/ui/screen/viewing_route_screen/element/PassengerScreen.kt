package com.example.traindriver.ui.screen.viewing_route_screen.element

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.traindriver.R
import com.example.traindriver.data.util.ResultState
import com.example.traindriver.domain.entity.Passenger
import com.example.traindriver.domain.entity.Route
import com.example.traindriver.ui.element_screen.LoadingElement
import com.example.traindriver.ui.screen.viewing_route_screen.ViewingRouteViewModel
import com.example.traindriver.ui.theme.ShapeBackground
import com.example.traindriver.ui.theme.Typography
import com.example.traindriver.ui.util.Constants.DURATION_CROSSFADE
import com.example.traindriver.ui.util.DateAndTimeFormat
import com.example.traindriver.ui.util.EmptyDataText.DEFAULT_STATION_NAME
import java.text.SimpleDateFormat

@Composable
fun PassengerScreen(viewModel: ViewingRouteViewModel) {
    Crossfade(
        targetState = viewModel.routeState,
        animationSpec = tween(durationMillis = DURATION_CROSSFADE)
    ) { state ->
        when (state) {
            is ResultState.Loading -> {
                LoadingScreen()
            }
            is ResultState.Success -> state.data?.let { route ->
                DataScreen(route)
            }
            is ResultState.Failure -> {
                FailureScreen()
            }
        }
    }
}

@Composable
private fun LoadingScreen() {
    LoadingElement()
}

@Composable
private fun DataScreen(route: Route) {
    val scrollState = rememberLazyListState()
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (shadow, data) = createRefs()

        AnimatedVisibility(
            modifier = Modifier.zIndex(1f),
            visible = !scrollState.isScrollInInitialState(),
            enter = fadeIn(animationSpec = tween(durationMillis = 300)),
            exit = fadeOut(animationSpec = tween(durationMillis = 300))
        ) {
            BottomShadow(modifier = Modifier.constrainAs(shadow) { top.linkTo(parent.top) })
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .constrainAs(data) {
                    top.linkTo(parent.top)
                }, state = scrollState
        ) {
            itemsIndexed(route.passengerList) { index, item ->
                PassengerItem(item)
                if (index == route.passengerList.lastIndex) {
                    Spacer(modifier = Modifier.height(60.dp))
                }
            }
        }
    }
}

@Composable
fun PassengerItem(passenger: Passenger) {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp),
        backgroundColor = MaterialTheme.colors.surface,
        shape = ShapeBackground.medium,
        elevation = 6.dp
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            val stArrivalText = passenger.stationArrival ?: DEFAULT_STATION_NAME
            val stDepartureText = passenger.stationDeparture ?: DEFAULT_STATION_NAME
            val timeArrivalText = passenger.timeArrival?.let { millis ->
                SimpleDateFormat(DateAndTimeFormat.TIME_FORMAT).format(millis)
            } ?: DateAndTimeFormat.DEFAULT_TIME_TEXT
            val timeDepartureText = passenger.timeDeparture?.let { millis ->
                SimpleDateFormat(DateAndTimeFormat.TIME_FORMAT).format(millis)
            } ?: DateAndTimeFormat.DEFAULT_TIME_TEXT
            val trainNumberText = "№ ${passenger.trainNumber ?: "0000"}"

            val (trainNumber, stArrival, stDeparture, timeArrival, timeDeparture, notes) = createRefs()

            createHorizontalChain(stArrival, stDeparture, chainStyle = ChainStyle.SpreadInside)
            createHorizontalChain(timeArrival, timeDeparture, chainStyle = ChainStyle.SpreadInside)

            Text(
                modifier = Modifier.constrainAs(trainNumber) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                },
                text = trainNumberText,
                style = Typography.subtitle1.copy(color = setTextColor(passenger.trainNumber))
            )

            Text(modifier = Modifier
                .constrainAs(stArrival) {
                    top.linkTo(trainNumber.bottom)
                }
                .padding(top = 8.dp), text = stArrivalText, style = Typography.body1.copy(
                color = setTextColor(passenger.stationArrival)
            ))

            Text(modifier = Modifier
                .constrainAs(stDeparture) {
                    top.linkTo(trainNumber.bottom)
                }
                .padding(top = 8.dp), text = stDepartureText, style = Typography.body1.copy(
                color = setTextColor(passenger.stationDeparture)
            ))

            Text(modifier = Modifier
                .constrainAs(timeArrival) {
                    top.linkTo(stArrival.bottom)
                }
                .padding(top = 8.dp), text = timeArrivalText, style = Typography.body1.copy(
                color = setTextColor(passenger.timeArrival)
            ))

            Text(modifier = Modifier
                .constrainAs(timeDeparture) {
                    top.linkTo(stDeparture.bottom)
                }
                .padding(top = 8.dp), text = timeDepartureText, style = Typography.body1.copy(
                color = setTextColor(passenger.timeDeparture)
            ))

            passenger.notes?.let { text ->
                Text(modifier = Modifier
                    .constrainAs(notes) {
                        start.linkTo(parent.start)
                        top.linkTo(timeArrival.bottom)
                    }
                    .padding(top = 8.dp),
                    text = text,
                    textAlign = TextAlign.Start,
                    style = Typography.body2.copy(color = MaterialTheme.colors.primary))
            }
        }
    }
}

@Composable
private fun FailureScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = stringResource(id = R.string.route_opening_error), style = Typography.h3)
    }
}
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.traindriver.R
import com.example.traindriver.data.util.ResultState
import com.example.traindriver.domain.entity.Passenger
import com.example.traindriver.domain.entity.Route
import com.example.traindriver.ui.element_screen.LoadingElement
import com.example.traindriver.ui.element_screen.SuperDivider
import com.example.traindriver.ui.screen.signin_screen.elements.SecondarySpacer
import com.example.traindriver.ui.screen.viewing_route_screen.RouteResponse
import com.example.traindriver.ui.theme.Typography
import com.example.traindriver.ui.util.Constants.DURATION_CROSSFADE
import com.example.traindriver.ui.util.DateAndTimeFormat
import com.example.traindriver.ui.util.EmptyDataText.DEFAULT_STATION_NAME
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PassengerScreen(response: RouteResponse) {
    Crossfade(
        targetState = response,
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
                if (index == 0){
                    SecondarySpacer()
                } else {
                    SuperDivider()
                }
                PassengerItem(item)
                if (index == route.passengerList.lastIndex) {
                    Spacer(modifier = Modifier.height(60.dp))
                }
            }
        }
    }
}

@Composable
private fun PassengerItem(passenger: Passenger) {
    val trainNumberText = "№ ${passenger.trainNumber ?: "0000"}"
    val stArrivalText = passenger.stationArrival ?: DEFAULT_STATION_NAME
    val stDepartureText = passenger.stationDeparture ?: DEFAULT_STATION_NAME
    val timeArrivalText = passenger.timeArrival?.let { millis ->
        SimpleDateFormat(DateAndTimeFormat.TIME_FORMAT, Locale.getDefault()).format(millis)
    } ?: DateAndTimeFormat.DEFAULT_TIME_TEXT
    val timeDepartureText = passenger.timeDeparture?.let { millis ->
        SimpleDateFormat(DateAndTimeFormat.TIME_FORMAT, Locale.getDefault()).format(millis)
    } ?: DateAndTimeFormat.DEFAULT_TIME_TEXT
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 18.dp)
    ) {
        Text(
            text = trainNumberText,
            style = Typography.titleLarge.copy(color = setTextColor(passenger.trainNumber))
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stDepartureText,
                    style = Typography.bodyLarge.copy(
                        color = setTextColor(passenger.stationDeparture)
                    )
                )
                Text(
                    text = timeDepartureText,
                    style = Typography.bodyLarge.copy(
                        color = setTextColor(passenger.timeDeparture)
                    )
                )
            }
            Row(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stArrivalText,
                    style = Typography.bodyLarge.copy(
                        color = setTextColor(passenger.stationArrival)
                    )
                )
                Text(
                    text = timeArrivalText,
                    style = Typography.bodyLarge.copy(
                        color = setTextColor(passenger.timeArrival)
                    )
                )
            }
        }
        passenger.notes?.let { text ->
            Text(
                modifier = Modifier
                    .padding(top = 12.dp),
                text = text,
                textAlign = TextAlign.Start,
                style = Typography.bodyMedium.copy(color = MaterialTheme.colorScheme.primary)
            )
        }
    }
}

@Composable
private fun FailureScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = stringResource(id = R.string.route_opening_error), style = Typography.displaySmall)
    }
}
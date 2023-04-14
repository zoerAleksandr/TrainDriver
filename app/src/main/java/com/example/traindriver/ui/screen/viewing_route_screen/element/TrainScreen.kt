package com.example.traindriver.ui.screen.viewing_route_screen.element

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.traindriver.R
import com.example.traindriver.data.util.ResultState
import com.example.traindriver.domain.entity.Route
import com.example.traindriver.domain.entity.Station
import com.example.traindriver.domain.entity.Train
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
fun TrainScreen(response: RouteResponse) {
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
            BottomShadow(
                modifier = Modifier.constrainAs(shadow) { top.linkTo(parent.top) }
            )
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .constrainAs(data) {
                    top.linkTo(parent.top)
                },
            state = scrollState
        ) {
            itemsIndexed(route.trainList) { index, item ->
                if(index == 0){
                    SecondarySpacer()
                } else {
                    SuperDivider()
                }
                TrainItem(item)
                if (index == route.trainList.lastIndex) {
                    Spacer(modifier = Modifier.height(60.dp))
                }
            }
        }
    }
}

@Composable
private fun FailureScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = stringResource(id = R.string.route_opening_error), style = Typography.labelLarge)
    }
}

@Composable
fun TrainItem(train: Train) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 24.dp)
    ) {
        val (number, weight, axle, conditionalLength, loco, stations) = createRefs()

        createHorizontalChain(weight, axle, conditionalLength, chainStyle = ChainStyle.Packed)
        Text(
            modifier = Modifier
                .padding(start = 32.dp, top = 16.dp)
                .constrainAs(number) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                },
            text = "№" + " ${train.number ?: "0000"}",
            style = Typography.titleLarge.copy(color = setTextColor(any = train.number))
        )
        Text(
            modifier = Modifier
                .padding(end = 32.dp, top = 16.dp)
                .constrainAs(loco) {
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                },
            text = "${train.locomotive?.series ?: ""} - ${train.locomotive?.number ?: ""}",
            style = Typography.titleLarge.copy(color = setTextColor(any = train.number))
        )
        Column(modifier = Modifier
            .constrainAs(weight) {
                top.linkTo(number.bottom)
                start.linkTo(parent.start)
            }
            .padding(end = 12.dp, top = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "вес",
                style = Typography.bodySmall.copy(color = MaterialTheme.colorScheme.secondary)
            )
            Text(
                text = "${train.weight ?: " - "}",
                style = Typography.bodyLarge.copy(color = setTextColor(any = train.weight))
            )
        }
        Column(modifier = Modifier
            .constrainAs(axle) {
                top.linkTo(number.bottom)
            }
            .padding(end = 12.dp, top = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "оси",
                style = Typography.bodySmall.copy(color = MaterialTheme.colorScheme.secondary)
            )
            Text(
                text = "${train.axle ?: " - "}",
                style = Typography.bodyLarge.copy(color = setTextColor(any = train.weight))
            )
        }
        Column(modifier = Modifier
            .constrainAs(conditionalLength) {
                top.linkTo(number.bottom)
            }
            .padding(end = 12.dp, top = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "у.д.",
                style = Typography.bodySmall.copy(color = MaterialTheme.colorScheme.secondary)
            )
            Text(
                text = "${train.conditionalLength ?: " - "}",
                style = Typography.bodyLarge.copy(color = setTextColor(any = train.weight))
            )
        }
        Column(modifier = Modifier
            .padding(vertical = 12.dp)
            .constrainAs(stations) {
                top.linkTo(weight.bottom)
            }
            .fillMaxWidth()
            .padding(top = 16.dp)) {
            train.stations.forEachIndexed { index, item ->
                if (index.rem(2) != 0) {
                    ItemStation(item, Color.Transparent)
                } else {
                    ItemStation(item, MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f))
                }
            }
        }
    }
}

@Composable
@ReadOnlyComposable
fun textColorOrTransparent(any: Any?): Color = if (any == null) {
    Color.Transparent
} else {
    MaterialTheme.colorScheme.primary
}

@Composable
fun ItemStation(station: Station, backgroundColor: Color) {
    ConstraintLayout(
        modifier = Modifier
            .background(backgroundColor)
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 32.dp)
    ) {
        val (name, time) = createRefs()

        val verticalGuideLine = createGuidelineFromStart(0.6f)

        val arrival = station.timeArrival?.let { millis ->
            SimpleDateFormat(DateAndTimeFormat.TIME_FORMAT, Locale.getDefault()).format(millis)
        } ?: DateAndTimeFormat.DEFAULT_TIME_TEXT

        val departure = station.timeDeparture?.let { millis ->
            SimpleDateFormat(DateAndTimeFormat.TIME_FORMAT, Locale.getDefault()).format(millis)
        } ?: DateAndTimeFormat.DEFAULT_TIME_TEXT

        val nameText = station.stationName ?: DEFAULT_STATION_NAME

        Text(
            modifier = Modifier
                .constrainAs(name) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    end.linkTo(verticalGuideLine)
                    width = Dimension.fillToConstraints
                }
                .padding(end = 4.dp),
            textAlign = TextAlign.Start,
            text = nameText,
            style = Typography.bodyLarge.copy(color = setTextColor(station.stationName)),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Row(
            modifier = Modifier
                .constrainAs(time) {
                    start.linkTo(verticalGuideLine)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    width = Dimension.fillToConstraints
                }
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {

            Text(
                text = arrival,
                style = Typography.bodyLarge.copy(color = textColorOrTransparent(station.timeArrival))
            )

            Text(
                text = " - ",
                color = if (station.timeArrival == null || station.timeDeparture == null) {
                    Color.Transparent
                } else {
                    MaterialTheme.colorScheme.primary
                },
                style = Typography.bodyLarge
            )
            Text(
                text = departure,
                style = Typography.bodyLarge.copy(color = textColorOrTransparent(station.timeDeparture))
            )

        }
    }
}


package com.example.traindriver.ui.screen.viewing_route_screen.element

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.traindriver.R
import com.example.traindriver.data.util.ResultState
import com.example.traindriver.domain.entity.Route
import com.example.traindriver.domain.entity.Station
import com.example.traindriver.domain.entity.Train
import com.example.traindriver.ui.element_screen.LoadingElement
import com.example.traindriver.ui.screen.viewing_route_screen.ViewingRouteViewModel
import com.example.traindriver.ui.theme.ShapeBackground
import com.example.traindriver.ui.theme.Typography
import com.example.traindriver.ui.util.DateAndTimeFormat
import com.example.traindriver.ui.util.EmptyDataText.DEFAULT_STATION_NAME
import java.text.SimpleDateFormat

@Composable
fun TrainScreen(viewModel: ViewingRouteViewModel) {
    when (val routeState = viewModel.routeState) {
        is ResultState.Loading -> {
            LoadingScreen()
        }
        is ResultState.Success -> routeState.data?.let { route ->
            DataScreen(route)
        }
        is ResultState.Failure -> {
            FailureScreen()
        }
    }
}

@Composable
private fun LoadingScreen() {
    LoadingElement()
}

@Composable
private fun DataScreen(route: Route) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        itemsIndexed(route.trainList) { index, item ->
            TrainItem(item)
            if (index == route.trainList.lastIndex) {
                Spacer(modifier = Modifier.height(60.dp))
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

@Composable
fun TrainItem(train: Train) {
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
            val (number, weight, axle, conditionalLength, loco, stations) = createRefs()

            createHorizontalChain(weight, axle, conditionalLength, chainStyle = ChainStyle.Packed)
            Text(
                modifier = Modifier.constrainAs(number) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                },
                text = "№" + " ${train.number ?: "0000"}",
                style = Typography.subtitle1.copy(color = setTextColor(any = train.number))
            )
            Text(
                modifier = Modifier.constrainAs(loco) {
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                },
                text = "${train.locomotive?.series ?: ""} - ${train.locomotive?.number ?: ""}",
                style = Typography.subtitle1.copy(color = setTextColor(any = train.number))
            )
            Column(modifier = Modifier
                .constrainAs(weight) {
                    top.linkTo(number.bottom)
                    start.linkTo(parent.start)
                }
                .padding(end = 12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "вес",
                    style = Typography.caption.copy(color = MaterialTheme.colors.primaryVariant)
                )
                Text(
                    text = "${train.weight ?: " - "}",
                    style = Typography.body1.copy(color = setTextColor(any = train.weight))
                )
            }
            Column(modifier = Modifier
                .constrainAs(axle) {
                    top.linkTo(number.bottom)
                }
                .padding(end = 12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "оси",
                    style = Typography.caption.copy(color = MaterialTheme.colors.primaryVariant)
                )
                Text(
                    text = "${train.axle ?: " - "}",
                    style = Typography.body1.copy(color = setTextColor(any = train.weight))
                )
            }
            Column(modifier = Modifier
                .constrainAs(conditionalLength) {
                    top.linkTo(number.bottom)
                }
                .padding(end = 12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "у.д.",
                    style = Typography.caption.copy(color = MaterialTheme.colors.primaryVariant)
                )
                Text(
                    text = "${train.conditionalLength ?: " - "}",
                    style = Typography.body1.copy(color = setTextColor(any = train.weight))
                )
            }
            Column(modifier = Modifier
                .constrainAs(stations) {
                    top.linkTo(weight.bottom)
                }
                .fillMaxWidth()
                .padding(top = 8.dp)) {
                train.stations.forEachIndexed { index, item ->
                    ItemStation(item)
                    if (index != train.stations.lastIndex) {
                        Divider(color = MaterialTheme.colors.primaryVariant.copy(alpha = 0.2f))
                    }
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
    MaterialTheme.colors.primary
}

@Composable
fun ItemStation(station: Station) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp, top = 6.dp)
    ) {
        val (name, time) = createRefs()

        val verticalGuideLine = createGuidelineFromStart(0.6f)

        val arrival = station.timeArrival?.let { millis ->
            SimpleDateFormat(DateAndTimeFormat.TIME_FORMAT).format(millis)
        } ?: DateAndTimeFormat.DEFAULT_TIME_TEXT

        val departure = station.timeDeparture?.let { millis ->
            SimpleDateFormat(DateAndTimeFormat.TIME_FORMAT).format(millis)
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
            style = Typography.body1.copy(color = setTextColor(station.stationName)),
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
                style = Typography.body1.copy(color = textColorOrTransparent(station.timeArrival))
            )

            Text(
                text = " - ",
                color = if (station.timeArrival == null || station.timeDeparture == null) {
                    Color.Transparent
                } else {
                    MaterialTheme.colors.primary
                },
                style = Typography.body1
            )
            Text(
                text = departure,
                style = Typography.body1.copy(color = textColorOrTransparent(station.timeDeparture))
            )

        }
    }
}


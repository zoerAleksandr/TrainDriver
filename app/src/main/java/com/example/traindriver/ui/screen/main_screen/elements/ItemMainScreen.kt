package com.example.traindriver.ui.screen.main_screen.elements

import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension.Companion.fillToConstraints
import com.example.traindriver.domain.entity.Route
import com.example.traindriver.ui.element_screen.VerticalDividerTrainDriver
import com.example.traindriver.ui.theme.ShapeBackground
import com.example.traindriver.ui.theme.TrainDriverTheme
import com.example.traindriver.ui.theme.Typography
import com.example.traindriver.ui.util.*

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ItemMainScreen(route: Route, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, start = 8.dp, end = 8.dp, bottom = 2.dp)
            .height(80.dp),
        backgroundColor = MaterialTheme.colors.background,
        shape = ShapeBackground.medium,
        elevation = 2.dp,
        onClick = onClick
    ) {
        ConstraintLayout {
            val (date, verticalDividerFirst, station, verticalDividerSecond, workTime) = createRefs()

            DateElementItem(
                modifier = Modifier
                    .padding(horizontal = 14.dp)
                    .constrainAs(date) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                    }, date = route.timeStartWork
            )
            VerticalDividerTrainDriver(modifier = Modifier
                .constrainAs(verticalDividerFirst) {
                    start.linkTo(date.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
                .padding(vertical = 10.dp),
                thickness = 0.5.dp)
            StationElementItem(modifier = Modifier
                .constrainAs(station) {
                    top.linkTo(parent.top)
                    start.linkTo(verticalDividerFirst.end)
                    end.linkTo(verticalDividerSecond.start)
                    bottom.linkTo(parent.bottom)
                    width = fillToConstraints
                    height = fillToConstraints
                }
                .padding(horizontal = 16.dp), route = route)
            VerticalDividerTrainDriver(modifier = Modifier
                .constrainAs(verticalDividerSecond) {
                    end.linkTo(workTime.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
                .padding(vertical = 10.dp, horizontal = 2.dp),
                thickness = 0.5.dp)
            WorkTimeElementItem(modifier = Modifier
                .constrainAs(workTime) {
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
                .padding(horizontal = 12.dp), route = route)
        }
    }
}

@Composable
fun WorkTimeElementItem(
    modifier: Modifier = Modifier, route: Route
) {
    val workTime = route.getWorkTime()

    val textTime = if (workTime != null) {
        val hour = workTime.getHour()
        val hourText = if (hour < 10) {
            "0$hour"
        } else {
            hour.toString()
        }

        val minute = workTime.getRemainingMinuteFromHour()
        val minuteText = if (minute < 10) {
            "0$minute"
        } else {
            minute.toString()
        }
        "$hourText:$minuteText"
    } else {
        "--:--"
    }
    Text(modifier = modifier, text = textTime, style = Typography.body2)
}

@Composable
fun StationElementItem(
    modifier: Modifier = Modifier, route: Route
) {
    val textFirstStation = if (route.stationList.isNotEmpty()) {
        route.stationList.first().stationName ?: ""
    } else {
        ""
    }

    val textLastStation =
        if (route.stationList.isNotEmpty() && route.stationList.size > 1) {
            "- ${route.stationList.last().stationName ?: ""}"
        } else {
            ""
        }
    val textStation = "$textFirstStation $textLastStation"

    val textLocoSeries = if (route.locoList.isNotEmpty()) {
        route.locoList.first().series ?: ""
    } else {
        ""
    }
    val textLocoNumber = if (route.locoList.isNotEmpty()) {
        "№${route.locoList.first().number ?: ""}"
    } else {
        ""
    }
    val dots = if (route.locoList.size > 1) {
        "..."
    } else {
        ""
    }

    val textLoco = "$textLocoSeries $textLocoNumber $dots"


    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = textStation,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            style = Typography.body2
        )
        Text(
            text = textLoco,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            style = Typography.body2
        )
    }
}

@Composable
fun DateElementItem(
    modifier: Modifier = Modifier, date: Long?
) {
    val day = getDay(date)
    val month = getMonth(date)

    Column(
        modifier = modifier
            .fillMaxHeight()
            .wrapContentWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = day, style = Typography.body2)
        Text(text = month, style = Typography.body2)
    }
}

@Composable
@DarkLightPreviews
@FontScalePreviews
private fun PreviewItem() {
    TrainDriverTheme {
        ItemMainScreen(
            Route(),
            {}
        )
    }
}
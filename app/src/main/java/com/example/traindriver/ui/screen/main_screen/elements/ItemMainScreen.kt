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
import com.example.traindriver.domain.entity.Itinerary
import com.example.traindriver.ui.element_screen.VerticalDividerTrainDriver
import com.example.traindriver.ui.theme.ShapeBackground
import com.example.traindriver.ui.theme.TrainDriverTheme
import com.example.traindriver.ui.util.*

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ItemMainScreen(itinerary: Itinerary, onClick: () -> Unit) {
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
                    }, date = itinerary.timeStartWork
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
                .padding(horizontal = 16.dp), itinerary = itinerary)
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
                .padding(horizontal = 12.dp), itinerary = itinerary)
        }
    }
}

@Composable
fun WorkTimeElementItem(
    modifier: Modifier = Modifier, itinerary: Itinerary
) {
    val workTime = itinerary.getWorkTime()

    val textTime = if (workTime != 0L) {
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
    Text(modifier = modifier, text = textTime)
}

@Composable
fun StationElementItem(
    modifier: Modifier = Modifier, itinerary: Itinerary
) {
    val textFirstStation = if (itinerary.stationList.isNotEmpty()) {
        itinerary.stationList.first().stationName ?: ""
    } else {
        ""
    }

    val textLastStation =
        if (itinerary.stationList.isNotEmpty() && itinerary.stationList.size > 1) {
            "- ${itinerary.stationList.last().stationName ?: ""}"
        } else {
            ""
        }
    val textStation = "$textFirstStation $textLastStation"

    val textLocoSeries = if (itinerary.locoList.isNotEmpty()) {
        itinerary.locoList.first().series ?: ""
    } else {
        ""
    }
    val textLocoNumber = if (itinerary.locoList.isNotEmpty()) {
        "№${itinerary.locoList.first().number ?: ""}"
    } else {
        ""
    }
    val dots = if (itinerary.locoList.size > 1) {
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
        Text(text = textStation, overflow = TextOverflow.Ellipsis, maxLines = 1)
        Text(text = textLoco, overflow = TextOverflow.Ellipsis, maxLines = 1)
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
        Text(text = day)
        Text(text = month)
    }
}

@Composable
@DarkLightPreviews
@FontScalePreviews
private fun PreviewItem() {
    TrainDriverTheme {
        ItemMainScreen(
            Itinerary(),
            {}
        )
    }
}
package com.example.traindriver.ui.screen.main_screen.elements

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.MarqueeAnimationMode
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension.Companion.fillToConstraints
import com.example.traindriver.domain.entity.Route
import com.example.traindriver.ui.element_screen.TextBodyLarge
import com.example.traindriver.ui.element_screen.VerticalDividerTrainDriver
import com.example.traindriver.ui.theme.ShapeBackground
import com.example.traindriver.ui.theme.TrainDriverTheme
import com.example.traindriver.ui.util.DarkLightPreviews
import com.example.traindriver.ui.util.FontScalePreviews
import com.example.traindriver.ui.util.getDay
import com.example.traindriver.ui.util.getMonth
import com.example.traindriver.ui.util.long_util.getTimeInStringFormat

@Composable
fun ItemMainScreen(route: Route, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, start = 8.dp, end = 8.dp, bottom = 2.dp)
            .height(80.dp)
            .clickable { onClick.invoke() },
        shape = ShapeBackground.medium,
    ) {
        ConstraintLayout(
            modifier = Modifier.fillMaxWidth()
        ) {
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
            StationElementItem(
                modifier = Modifier
                    .constrainAs(station) {
                        top.linkTo(parent.top)
                        start.linkTo(verticalDividerFirst.end)
                        end.linkTo(verticalDividerSecond.start)
                        bottom.linkTo(parent.bottom)
                        width = fillToConstraints
                        height = fillToConstraints
                    }
                    .padding(horizontal = 16.dp),
                route = route
            )
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
    val textTime = workTime.getTimeInStringFormat()
    TextBodyLarge(modifier = modifier, text = textTime)
}

@OptIn(ExperimentalFoundationApi::class)
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
        TextBodyLarge(
            modifier = Modifier.basicMarquee(animationMode = MarqueeAnimationMode.WhileFocused),
            text = textStation,
            overflow = TextOverflow.Ellipsis,
        )
        TextBodyLarge(
            modifier = Modifier.basicMarquee(animationMode = MarqueeAnimationMode.WhileFocused),
            text = textLoco,
            overflow = TextOverflow.Ellipsis,
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
        TextBodyLarge(text = day)
        TextBodyLarge(text = month)
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
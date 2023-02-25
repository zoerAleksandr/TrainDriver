package com.example.traindriver.ui.screen.viewing_route_screen.element

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.traindriver.R
import com.example.traindriver.data.util.ResultState
import com.example.traindriver.domain.entity.Route
import com.example.traindriver.ui.element_screen.LoadingElement
import com.example.traindriver.ui.screen.Screen
import com.example.traindriver.ui.screen.viewing_route_screen.RouteResponse
import com.example.traindriver.ui.theme.ColorClicableText
import com.example.traindriver.ui.theme.ShapeBackground
import com.example.traindriver.ui.theme.TrainDriverTheme
import com.example.traindriver.ui.theme.Typography
import com.example.traindriver.ui.util.DarkLightPreviews
import com.example.traindriver.ui.util.DateAndTimeFormat.DATE_FORMAT
import com.example.traindriver.ui.util.DateAndTimeFormat.DEFAULT_DATE_TEXT
import com.example.traindriver.ui.util.DateAndTimeFormat.DEFAULT_TIME_TEXT
import com.example.traindriver.ui.util.DateAndTimeFormat.TIME_FORMAT
import com.example.traindriver.ui.util.getHour
import com.example.traindriver.ui.util.getRemainingMinuteFromHour
import java.text.SimpleDateFormat

private const val LINK_TO_SETTING = "LINK_TO_SETTING"

operator fun Long?.plus(other: Long?): Long? =
    if (this != null && other != null) {
        this + other
    } else {
        null
    }

@Composable
fun WorkTimeScreen(navController: NavController, routeResponse: RouteResponse, minTimeRest: Long) {
    when (routeResponse) {
        is ResultState.Loading -> {
            LoadingScreen()
        }
        is ResultState.Success -> routeResponse.data?.let { route ->
            DataScreen(route, navController, minTimeRest)
        }
        is ResultState.Failure -> {
            FailureScreen()
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
private fun DataScreen(route: Route, navController: NavController, minTimeRest: Long) {
    val isDeterminateStartTime = route.timeStartWork != null
    val isDeterminateEndTime = route.timeEndWork != null

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 54.dp)
    ) {
        val (startTimeBlock, endTimeBlock, overTimeBlock, typeOfRest) = createRefs()

        createHorizontalChain(
            startTimeBlock, endTimeBlock, chainStyle = ChainStyle.Spread
        )
        val minTimeRestText = (minTimeRest / 3_600_000f).let { time ->
            if (time % 1 == 0f) {
                time.toInt()
            } else {
                time
            }
        }

        val link = buildAnnotatedString {
            val text = stringResource(id = R.string.info_text_min_time_rest, minTimeRestText)

            val endIndex = text.length - 1
            val startIndex = startIndexLastWord(text)

            append(text)
            addStyle(
                style = SpanStyle(
                    color = ColorClicableText,
                    textDecoration = TextDecoration.Underline
                ), start = startIndex, end = endIndex
            )

            addStringAnnotation(
                tag = LINK_TO_SETTING,
                annotation = Screen.Setting.route,
                start = startIndex,
                end = endIndex
            )
        }
        val minRest: Long? = route.timeEndWork + minTimeRest
        val completeRest: Long? = route.timeEndWork + route.getWorkTime()

        Column(
            modifier = Modifier
                .constrainAs(startTimeBlock) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                }
                .border(
                    width = 0.5.dp,
                    shape = ShapeBackground.medium,
                    color = MaterialTheme.colors.secondary
                )
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val dateStartText = route.timeStartWork?.let { millis ->
                SimpleDateFormat(DATE_FORMAT).format(millis)
            } ?: DEFAULT_DATE_TEXT
            val timeStartText = route.timeStartWork?.let { millis ->
                SimpleDateFormat(TIME_FORMAT).format(millis)
            } ?: DEFAULT_TIME_TEXT

            Text(
                text = dateStartText,
                style = Typography.body1,
                color = if (isDeterminateStartTime) {
                    MaterialTheme.colors.primary
                } else {
                    MaterialTheme.colors.primaryVariant
                }
            )
            Text(
                text = timeStartText,
                style = Typography.body1,
                color = if (isDeterminateStartTime) {
                    MaterialTheme.colors.primary
                } else {
                    MaterialTheme.colors.primaryVariant
                }
            )
        }

        Column(
            modifier = Modifier
                .constrainAs(endTimeBlock) {
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                }
                .border(
                    width = 0.5.dp,
                    shape = ShapeBackground.medium,
                    color = MaterialTheme.colors.secondary
                )
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val dateEndText = route.timeEndWork?.let { millis ->
                SimpleDateFormat(DATE_FORMAT).format(millis)
            } ?: DEFAULT_DATE_TEXT

            val timeEndText = route.timeEndWork?.let { millis ->
                SimpleDateFormat(TIME_FORMAT).format(millis)
            } ?: DEFAULT_TIME_TEXT

            Text(
                text = dateEndText,
                style = Typography.body1,
                color = if (isDeterminateEndTime) {
                    MaterialTheme.colors.primary
                } else {
                    MaterialTheme.colors.primaryVariant
                }
            )
            Text(
                text = timeEndText,
                style = Typography.body1,
                color = if (isDeterminateEndTime) {
                    MaterialTheme.colors.primary
                } else {
                    MaterialTheme.colors.primaryVariant
                }
            )
        }

        Column(
            modifier = Modifier
                .constrainAs(typeOfRest) {
                    start.linkTo(parent.start)
                    bottom.linkTo(parent.bottom)
                }
                .padding(start = 32.dp, bottom = 64.dp),
            horizontalAlignment = Alignment.Start) {

            minRest?.let { SimpleDateFormat("$DATE_FORMAT $TIME_FORMAT").format(it) }
                ?.also {
                    Text(
                        text = stringResource(id = R.string.min_time_rest_text, it),
                        style = Typography.body2
                    )
                }

            completeRest?.let { SimpleDateFormat("$DATE_FORMAT $TIME_FORMAT").format(it) }
                ?.also {
                    Text(
                        text = stringResource(id = R.string.complete_time_rest_text, it),
                        style = Typography.body2
                    )
                }

            ClickableText(
                modifier = Modifier.padding(top = 12.dp),
                text = link,
                style = Typography.caption
                    .copy(fontStyle = FontStyle.Italic, color = MaterialTheme.colors.onBackground)
            ) {
                link.getStringAnnotations(LINK_TO_SETTING, it, it)
                    .firstOrNull()?.let { stringAnnotation ->
                        navController.navigate(stringAnnotation.item)
                    }
            }
        }

        Box(
            modifier = Modifier
                .constrainAs(overTimeBlock) {
                    top.linkTo(startTimeBlock.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .padding(top = 32.dp)
        ) {
            val millis = route.getWorkTime()
            val textOverTime = if (millis != null) {
                val hour = millis.getHour()
                val hourText = if (hour < 10) {
                    "0$hour"
                } else {
                    hour.toString()
                } + stringResource(id = R.string.abbreviated_hour)

                val minute = millis.getRemainingMinuteFromHour()
                val minuteText = if (minute < 10) {
                    "0$minute"
                } else {
                    minute.toString()
                } + stringResource(id = R.string.abbreviated_minute)

                "$hourText $minuteText"
            } else {
                ""
            }
            Text(text = textOverTime, style = Typography.body2)
        }
    }
}

@Composable
private fun LoadingScreen() {
    LoadingElement()
}

fun startIndexLastWord(text: String): Int {
    val overLength = text.length
    for (index in overLength - 1 downTo 0) {
        if (text[index] == ' ') {
            return index + 1
        }
    }
    return overLength
}

@Composable
@DarkLightPreviews
private fun WorkTimeScreenPrev() {
    TrainDriverTheme {
        WorkTimeScreen(rememberNavController(), ResultState.Success(Route()), 1L)
    }
}
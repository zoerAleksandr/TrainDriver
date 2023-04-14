package com.example.traindriver.ui.screen.viewing_route_screen.element

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.example.traindriver.ui.element_screen.TextBodyLarge
import com.example.traindriver.ui.screen.Screen
import com.example.traindriver.ui.screen.viewing_route_screen.RouteResponse
import com.example.traindriver.ui.theme.ShapeBackground
import com.example.traindriver.ui.theme.TrainDriverTheme
import com.example.traindriver.ui.theme.Typography
import com.example.traindriver.ui.util.Constants.DURATION_CROSSFADE
import com.example.traindriver.ui.util.DarkLightPreviews
import com.example.traindriver.ui.util.DateAndTimeFormat.DATE_FORMAT
import com.example.traindriver.ui.util.DateAndTimeFormat.DEFAULT_DATE_TEXT
import com.example.traindriver.ui.util.DateAndTimeFormat.DEFAULT_TIME_TEXT
import com.example.traindriver.ui.util.DateAndTimeFormat.TIME_FORMAT
import com.example.traindriver.ui.util.long_util.div
import com.example.traindriver.ui.util.long_util.getTimeInStringFormat
import com.example.traindriver.ui.util.long_util.plus
import java.text.SimpleDateFormat
import java.util.*

const val LINK_TO_SETTING = "LINK_TO_SETTING"

@Composable
fun WorkTimeScreen(navController: NavController, routeResponse: RouteResponse, minTimeRest: Long) {
    Crossfade(
        targetState = routeResponse,
        animationSpec = tween(durationMillis = DURATION_CROSSFADE)
    ) { state ->
        when (state) {
            is ResultState.Loading -> {
                LoadingScreen()
            }
            is ResultState.Success -> state.data?.let { route ->
                DataScreen(route, navController, minTimeRest)
            }
            is ResultState.Failure -> {
                FailureScreen()
            }
        }
    }
}

@Composable
private fun FailureScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = stringResource(id = R.string.route_opening_error), style = Typography.displaySmall)
    }
}

@Composable
private fun DataScreen(route: Route, navController: NavController, minTimeRest: Long) {
    val isDeterminateStartTime = route.timeStartWork != null
    val isDeterminateEndTime = route.timeEndWork != null

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
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
                    color = MaterialTheme.colorScheme.tertiary,
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
        val halfRest = route.getWorkTime() / 2
        val minRest: Long? = halfRest?.let { half ->
            if (half > minTimeRest) {
                route.timeEndWork + half
            } else {
                route.timeEndWork + minTimeRest
            }
        }
        val completeRest: Long? = route.timeEndWork + route.getWorkTime()

        Column(
            modifier = Modifier
                .padding(top = 32.dp)
                .constrainAs(startTimeBlock) {
                    top.linkTo(overTimeBlock.bottom)
                    start.linkTo(parent.start)
                }
                .border(
                    width = 0.5.dp,
                    shape = ShapeBackground.small,
                    color = MaterialTheme.colorScheme.secondary
                )
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val dateStartText = route.timeStartWork?.let { millis ->
                SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(millis)
            } ?: DEFAULT_DATE_TEXT
            val timeStartText = route.timeStartWork?.let { millis ->
                SimpleDateFormat(TIME_FORMAT, Locale.getDefault()).format(millis)
            } ?: DEFAULT_TIME_TEXT

            TextBodyLarge(
                text = dateStartText,
                color = if (isDeterminateStartTime) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onPrimary
                }
            )
            TextBodyLarge(
                text = timeStartText,
                color = if (isDeterminateStartTime) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onPrimary
                }
            )
        }

        Column(
            modifier = Modifier
                .padding(top = 32.dp)
                .constrainAs(endTimeBlock) {
                    top.linkTo(overTimeBlock.bottom)
                    end.linkTo(parent.end)
                }
                .border(
                    width = 0.5.dp,
                    shape = ShapeBackground.small,
                    color = MaterialTheme.colorScheme.secondary
                )
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val dateEndText = route.timeEndWork?.let { millis ->
                SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(millis)
            } ?: DEFAULT_DATE_TEXT

            val timeEndText = route.timeEndWork?.let { millis ->
                SimpleDateFormat(TIME_FORMAT, Locale.getDefault()).format(millis)
            } ?: DEFAULT_TIME_TEXT

            TextBodyLarge(
                text = dateEndText,
                color = if (isDeterminateEndTime) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onPrimary
                }
            )
            TextBodyLarge(
                text = timeEndText,
                color = if (isDeterminateEndTime) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onPrimary
                }
            )
        }

        Column(
            modifier = Modifier
                .constrainAs(typeOfRest) {
                    start.linkTo(parent.start)
                    bottom.linkTo(parent.bottom)
                }
                .padding(start = 32.dp, end  = 32.dp, bottom = 64.dp),
            horizontalAlignment = Alignment.Start) {

            minRest?.let { SimpleDateFormat("$DATE_FORMAT $TIME_FORMAT", Locale.getDefault()).format(it) }
                ?.also {
                    TextBodyLarge(
                        text = stringResource(id = R.string.min_time_rest_text, it),
                    )
                }

            completeRest?.let { SimpleDateFormat("$DATE_FORMAT $TIME_FORMAT", Locale.getDefault()).format(it) }
                ?.also {
                    TextBodyLarge(
                        text = stringResource(id = R.string.complete_time_rest_text, it),
                    )
                }

            ClickableText(
                modifier = Modifier.padding(top = 12.dp),
                text = link,
                style = Typography.bodyMedium
                    .copy(fontStyle = FontStyle.Italic, color = MaterialTheme.colorScheme.onBackground)
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
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .padding(top = 52.dp)
        ) {
            val millis = route.getWorkTime()
            Text(text = millis.getTimeInStringFormat(), style = Typography.displaySmall)
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
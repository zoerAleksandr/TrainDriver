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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.traindriver.domain.entity.Route
import com.example.traindriver.ui.screen.Screen
import com.example.traindriver.ui.theme.ColorClicableText
import com.example.traindriver.ui.theme.ShapeBackground
import com.example.traindriver.ui.theme.TrainDriverTheme
import com.example.traindriver.ui.theme.Typography
import com.example.traindriver.ui.util.DarkLightPreviews
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
fun WorkTimeScreen(navController: NavController, route: Route) {
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

        val link = buildAnnotatedString {
            val text =
                "Время минимального отдыха составляет 3 часа. Изменить это время можно в настройках."

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
        val minRest : Long? = route.timeEndWork + 10_800_000L
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
                SimpleDateFormat("dd.MM.yyyy").format(millis)
            } ?: "00.00.0000"
            val timeStartText = route.timeStartWork?.let { millis ->
                SimpleDateFormat("hh:mm").format(millis)
            } ?: "00:00"

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
                SimpleDateFormat("dd.MM.yyyy").format(millis)
            } ?: "00.00.0000"

            val timeEndText = route.timeEndWork?.let { millis ->
                SimpleDateFormat("hh:mm").format(millis)
            } ?: "00:00"

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
            val minRestText = SimpleDateFormat("dd.MM hh:mm").format(minRest)
            val completeRestText = SimpleDateFormat("dd.MM hh:mm").format(completeRest)

            Text(text = "Минимальный отдых до $minRestText", style = Typography.body2)
            Text(text = "Полный отдых до $completeRestText", style = Typography.body2)

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
                }

                val minute = millis.getRemainingMinuteFromHour()
                val minuteText = if (minute < 10) {
                    "0$minute"
                } else {
                    minute.toString()
                }
                "${hourText}ч ${minuteText}мин"
            } else {
                ""
            }
            Text(text = textOverTime, style = Typography.subtitle1)
        }
    }
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
        WorkTimeScreen(rememberNavController(), Route())
    }
}
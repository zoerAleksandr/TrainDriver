package com.example.traindriver.ui.screen.adding_screen

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.traindriver.R
import com.example.traindriver.ui.element_screen.HorizontalDividerTrainDriver
import com.example.traindriver.ui.screen.Screen
import com.example.traindriver.ui.screen.viewing_route_screen.element.LINK_TO_SETTING
import com.example.traindriver.ui.screen.viewing_route_screen.element.setTextColor
import com.example.traindriver.ui.screen.viewing_route_screen.element.startIndexLastWord
import com.example.traindriver.ui.theme.ShapeBackground
import com.example.traindriver.ui.theme.Typography
import com.example.traindriver.ui.util.ClickableTextTrainDriver
import com.example.traindriver.ui.util.DateAndTimeFormat
import com.example.traindriver.ui.util.DateAndTimeFormat.DEFAULT_DATE_TEXT
import com.example.traindriver.ui.util.OnLifecycleEvent
import com.example.traindriver.ui.util.Tags.LINK_TO_HOME
import com.example.traindriver.ui.util.long_util.div
import com.example.traindriver.ui.util.long_util.getTimeInStringFormat
import com.example.traindriver.ui.util.long_util.minus
import com.example.traindriver.ui.util.long_util.plus
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar.*

@Composable
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
fun AddingScreen(viewModel: AddingViewModel = viewModel(), navController: NavController) {

    OnLifecycleEvent { _, event ->
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                viewModel.getMinTimeRest()
            }
            else -> {}
        }
    }

    var number by remember { mutableStateOf(TextFieldValue("")) }

    val timeValue = viewModel.state.value

    val dateStart = timeValue.startTime.time
    val dateEnd = timeValue.endTime.time

    val timeResult = dateEnd - dateStart

    val startCalendar = getInstance()

    dateStart?.let {
        startCalendar.timeInMillis = it
    }

    val startTimePicker = TimePickerDialog(
        LocalContext.current, { _, h: Int, m: Int ->
            startCalendar[HOUR_OF_DAY] = h
            startCalendar[MINUTE] = m
            startCalendar[SECOND] = 0
            startCalendar[MILLISECOND] = 0
            viewModel.createEvent(WorkTimeEvent.EnteredStartTime(startCalendar.timeInMillis))
            viewModel.createEvent(WorkTimeEvent.FocusChange(WorkTimeType.START))
        }, startCalendar[HOUR_OF_DAY], startCalendar[MINUTE], true
    )

    val startDatePicker = DatePickerDialog(
        LocalContext.current,
        { _, y: Int, m: Int, d: Int ->
            startCalendar[YEAR] = y
            startCalendar[MONTH] = m
            startCalendar[DAY_OF_MONTH] = d
            startTimePicker.show()
        }, startCalendar[YEAR], startCalendar[MONTH], startCalendar[DAY_OF_MONTH]
    )

    val endCalendar = getInstance()
    dateEnd?.let {
        endCalendar.timeInMillis = it
    }

    val endTimePicker = TimePickerDialog(
        LocalContext.current, { _, h: Int, m: Int ->
            endCalendar[HOUR_OF_DAY] = h
            endCalendar[MINUTE] = m
            endCalendar[SECOND] = 0
            endCalendar[MILLISECOND] = 0
            viewModel.createEvent(WorkTimeEvent.EnteredEndTime(endCalendar.timeInMillis))
            viewModel.createEvent(WorkTimeEvent.FocusChange(WorkTimeType.END))
        }, endCalendar[HOUR_OF_DAY], endCalendar[MINUTE], true
    )

    val endDatePicker = DatePickerDialog(
        LocalContext.current,
        { _, y: Int, m: Int, d: Int ->
            endCalendar[YEAR] = y
            endCalendar[MONTH] = m
            endCalendar[DAY_OF_MONTH] = d
            endTimePicker.show()
        }, endCalendar[YEAR], endCalendar[MONTH], endCalendar[DAY_OF_MONTH]
    )

    Scaffold(
        backgroundColor = MaterialTheme.colors.background,
        topBar = { AddingAppBar(navController = navController, enabled = timeValue.formValid) }
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            val (times, list, resultTime, restSwitch, info) = createRefs()

            Row(
                modifier = Modifier
                    .padding(24.dp)
                    .constrainAs(resultTime) {
                        top.linkTo(parent.top)
                        bottom.linkTo(times.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!timeValue.formValid) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_error_24),
                        tint = Color.Red,
                        contentDescription = null
                    )
                    Text(
                        modifier = Modifier.padding(start = 8.dp),
                        text = timeValue.errorMessage,
                        style = Typography.caption.copy(color = Color.Red),
                        color = Color.Red
                    )
                } else {
                    Text(
                        text = timeResult.getTimeInStringFormat(),
                        style = Typography.h1.copy(color = MaterialTheme.colors.primary)
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(times) {
                        top.linkTo(resultTime.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(
                    modifier = Modifier
                        .clickable {
                            startDatePicker.show()
                        }
                        .border(
                            width = 0.5.dp,
                            shape = ShapeBackground.small,
                            color = MaterialTheme.colors.secondary
                        )
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val dateStartText = dateStart?.let { millis ->
                        SimpleDateFormat(DateAndTimeFormat.DATE_FORMAT, Locale.getDefault()).format(
                            millis
                        )
                    } ?: DEFAULT_DATE_TEXT

                    Text(
                        text = dateStartText,
                        style = Typography.body1,
                        color = setTextColor(dateStart)
                    )
                    dateStart?.let { millis ->
                        val time =
                            SimpleDateFormat(
                                DateAndTimeFormat.TIME_FORMAT,
                                Locale.getDefault()
                            ).format(millis)
                        Text(
                            text = time,
                            style = Typography.body1,
                            color = setTextColor(dateStart)
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .border(
                            width = 0.5.dp,
                            shape = ShapeBackground.small,
                            color = MaterialTheme.colors.secondary
                        )
                        .padding(18.dp)
                        .clickable {
                            endDatePicker.show()
                        },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val dateStartText = dateEnd?.let { millis ->
                        SimpleDateFormat(DateAndTimeFormat.DATE_FORMAT, Locale.getDefault()).format(
                            millis
                        )
                    } ?: DEFAULT_DATE_TEXT

                    Text(
                        text = dateStartText,
                        style = Typography.body1,
                        color = setTextColor(dateEnd)
                    )
                    dateEnd?.let { millis ->
                        val time =
                            SimpleDateFormat(
                                DateAndTimeFormat.TIME_FORMAT,
                                Locale.getDefault()
                            ).format(millis)
                        Text(
                            text = time,
                            style = Typography.body1,
                            color = setTextColor(dateEnd)
                        )
                    }
                }
            }

            val checkedState = remember { mutableStateOf(false) }

            Row(
                modifier = Modifier
                    .padding(end = 16.dp, top = 24.dp)
                    .constrainAs(restSwitch) {
                        top.linkTo(times.bottom)
                        end.linkTo(parent.end)
                    },
                verticalAlignment = Alignment.CenterVertically
            )
            {
                Text(
                    modifier = Modifier
                        .padding(end = 8.dp),
                    text = "Отдых в ПО",
                    style = Typography.body2.copy(
                        color = if (checkedState.value) {
                            MaterialTheme.colors.primary
                        } else {
                            MaterialTheme.colors.primaryVariant
                        }
                    )
                )

                Switch(
                    checked = checkedState.value,
                    onCheckedChange = {
                        checkedState.value = it
                    },
                )
            }

            val halfRest = timeResult / 2
            val minRest: Long? = halfRest?.let { half ->
                if (half > viewModel.minTimeRest) {
                    dateEnd + half
                } else {
                    dateEnd + viewModel.minTimeRest
                }
            }
            val completeRest: Long? = dateEnd + timeResult
            val minTimeRestText = (viewModel.minTimeRest / 3_600_000f).let { time ->
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
                        color = MaterialTheme.colors.secondaryVariant,
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

            Column(
                modifier = Modifier
                    .constrainAs(info) {
                        start.linkTo(parent.start)
                        top.linkTo(restSwitch.bottom)
                    }
                    .padding(start = 16.dp),
                horizontalAlignment = Alignment.Start) {
                if (checkedState.value) {
/*                    minRest?.let {
                        SimpleDateFormat("${DateAndTimeFormat.DATE_FORMAT} ${DateAndTimeFormat.TIME_FORMAT}").format(
                            it
                        )
                    }
                        ?.also {
                            Text(
                                text = stringResource(id = R.string.min_time_rest_text, it),
                                style = Typography.body2
                            )
                        }

                    completeRest?.let {
                        SimpleDateFormat("${DateAndTimeFormat.DATE_FORMAT} ${DateAndTimeFormat.TIME_FORMAT}").format(
                            it
                        )
                    }
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
                            .copy(
                                fontStyle = FontStyle.Italic,
                                color = MaterialTheme.colors.onBackground
                            )
                    ) {
                        link.getStringAnnotations(LINK_TO_SETTING, it, it)
                            .firstOrNull()?.let { stringAnnotation ->
                                navController.navigate(stringAnnotation.item)
                            }
                    }*/
                }
            }

            Column(modifier = Modifier
                .constrainAs(list) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(info.bottom)
                    width = Dimension.fillToConstraints
                }
                .padding(vertical = 32.dp)) {
                NumberEditItem(
                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 24.dp),
                    value = number,
                    onValueChange = { number = it }
                )
                HorizontalDividerTrainDriver(modifier = Modifier.padding(horizontal = 24.dp))
                Spacer(modifier = Modifier.height(24.dp))
                ItemAddLoco()
                HorizontalDividerTrainDriver(modifier = Modifier.padding(horizontal = 24.dp))
                ItemAddLoco()
                HorizontalDividerTrainDriver(modifier = Modifier.padding(horizontal = 24.dp))
                ItemAddLoco()
            }
        }
    }
}

@Composable
fun NumberEditItem(
    modifier: Modifier = Modifier,
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit
) {
    BasicTextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        textStyle = Typography.subtitle2.copy(color = MaterialTheme.colors.primary),
        decorationBox = { innerTextField ->
            Box(
                contentAlignment = Alignment.CenterStart
            ) {
                if (value.text.isEmpty()) {
                    Text(
                        text = "Номер маршрута",
                        style = Typography.subtitle2,
                        color = MaterialTheme.colors.primaryVariant
                    )
                }
                innerTextField()
            }
        }
    )
}

@Composable
fun ItemAddLoco() {
    Card(
        backgroundColor = Color.Transparent,
        elevation = 0.dp
    ) {
        ConstraintLayout(
            modifier = Modifier
                .clickable { }
                .padding(vertical = 16.dp, horizontal = 24.dp)
                .fillMaxWidth()
        ) {
            val (title, data, icon) = createRefs()
            createVerticalChain(title, data, chainStyle = ChainStyle.SpreadInside)
            Text(
                modifier = Modifier
                    .constrainAs(title) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        bottom.linkTo(data.top)
                    }
                    .padding(bottom = 8.dp),
                text = "Локомотив",
                style = Typography.subtitle2.copy(color = MaterialTheme.colors.primary)
            )
            Text(
                modifier = Modifier
                    .constrainAs(data) {
                        start.linkTo(parent.start)
                        bottom.linkTo(parent.bottom)
                    },
                text = "2эс4к №001",
                style = Typography.body2.copy(color = MaterialTheme.colors.primaryVariant)
            )
            Image(
                modifier = Modifier
                    .size(20.dp)
                    .constrainAs(icon) {
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                        top.linkTo(parent.top)
                    },
                painter = painterResource(id = R.drawable.ic_forward_24),
                contentDescription = null,
                colorFilter = ColorFilter.tint(MaterialTheme.colors.secondary)
            )
        }
    }
}

@Composable
private fun AddingAppBar(navController: NavController, enabled: Boolean) {
    TopAppBar(
        modifier = Modifier
            .fillMaxHeight(0.12f),
        backgroundColor = MaterialTheme.colors.background
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 12.dp, end = 16.dp, bottom = 6.dp)
        ) {
            val (icon, button) = createRefs()
            IconButton(
                modifier = Modifier
                    .size(dimensionResource(id = R.dimen.min_size_view))
                    .constrainAs(icon) {
                        start.linkTo(parent.start)
                        bottom.linkTo(parent.bottom)
                    },
                onClick = {
                    navController.navigateUp()
                }
            ) {
                Image(
                    modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size)),
                    painter = painterResource(id = R.drawable.ic_back_24),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(MaterialTheme.colors.primary)
                )
            }

            val linkText = buildAnnotatedString {
                val text = "Сохранить"
                val start = 0
                val end = text.length
                append(text)

                addStringAnnotation(
                    tag = LINK_TO_HOME,
                    annotation = Screen.Home.route,
                    start = start,
                    end = end
                )
            }

            ClickableTextTrainDriver(
                modifier = Modifier.constrainAs(button) {
                    end.linkTo(parent.end)
                    top.linkTo(icon.top)
                    bottom.linkTo(icon.bottom)
                },
                enabled = enabled,
                text = linkText,
                style = Typography.button
            ) {
                // TODO SAVE TO REPOSITORY
                linkText.getStringAnnotations(LINK_TO_HOME, it, it)
                    .firstOrNull()?.let { stringAnnotation ->
                        navController.navigate(stringAnnotation.item)
                    }
            }
        }
    }
}
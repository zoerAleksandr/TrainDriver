package com.example.traindriver.ui.screen.adding_screen

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material3.AssistChip
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.traindriver.R
import com.example.traindriver.domain.entity.Locomotive
import com.example.traindriver.ui.element_screen.HorizontalDividerTrainDriver
import com.example.traindriver.ui.screen.Screen
import com.example.traindriver.ui.screen.adding_screen.bottom_sheet_screen.*
import com.example.traindriver.ui.screen.adding_screen.bottom_sheet_screen.adding_loco.AddingLocoScreen
import com.example.traindriver.ui.screen.adding_screen.state_holder.WorkTimeEvent
import com.example.traindriver.ui.screen.adding_screen.state_holder.WorkTimeType
import com.example.traindriver.ui.screen.viewing_route_screen.element.LINK_TO_SETTING
import com.example.traindriver.ui.screen.viewing_route_screen.element.setTextColor
import com.example.traindriver.ui.screen.viewing_route_screen.element.startIndexLastWord
import com.example.traindriver.ui.theme.ShapeBackground
import com.example.traindriver.ui.theme.ShapeSurface
import com.example.traindriver.ui.theme.Typography
import com.example.traindriver.ui.util.DateAndTimeFormat
import com.example.traindriver.ui.util.DateAndTimeFormat.DEFAULT_DATE_TEXT
import com.example.traindriver.ui.util.OnLifecycleEvent
import com.example.traindriver.ui.util.long_util.div
import com.example.traindriver.ui.util.long_util.getTimeInStringFormat
import com.example.traindriver.ui.util.long_util.minus
import com.example.traindriver.ui.util.long_util.plus
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.ExperimentalComposeUiApi
import com.example.traindriver.ui.util.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
fun AddingScreen(viewModel: AddingViewModel = viewModel(), navController: NavController) {

    val uid = navController.currentBackStackEntry?.arguments?.getString(Constants.ROUTE)

    OnLifecycleEvent { _, event ->
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                viewModel.getMinTimeRest()
            }
            else -> {}
        }
    }

    val number = viewModel.numberRouteState

    val timeValue = viewModel.timeEditState.value

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
    val scope = rememberCoroutineScope()
    var confirmStateChange by remember {
        mutableStateOf(false)
    }

    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = SheetState(
            skipPartiallyExpanded = false,
            confirmValueChange = {
                confirmStateChange
            }
        ),
    )

    /**
     * Изменятся параметр bottomSheetState в зависимости от положения шторки
     */
//    val screenHeight = LocalConfiguration.current.screenHeightDp
//    val offset = scaffoldState.bottomSheetState.offset.value
//    confirmStateChange = offset > screenHeight.times(1.4)

    var currentBottomSheet: BottomSheetScreen? by remember {
        mutableStateOf(null)
    }
    if (!scaffoldState.bottomSheetState.isVisible) currentBottomSheet = null

    val closeSheet: () -> Unit = {
        scope.launch {
            scaffoldState.bottomSheetState.hide()
        }
    }

    val openSheet: (BottomSheetScreen) -> Unit = { screen ->
        scope.launch {
            currentBottomSheet = screen
            scaffoldState.bottomSheetState.expand()
        }
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 0.dp,
        sheetShape = ShapeSurface.medium,
        sheetContent = {
            currentBottomSheet?.let { sheet ->
                SheetLayout(sheet, closeSheet, viewModel)
            }
        }
    ) {
        Scaffold(
//            topBar = { AddingAppBar(navController = navController, enabled = timeValue.formValid) }
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
                            style = Typography.bodySmall.copy(color = Color.Red),
                            color = Color.Red
                        )
                    } else {
                        Text(
                            text = timeResult.getTimeInStringFormat(),
                            style = Typography.headlineLarge.copy(color = MaterialTheme.colorScheme.primary)
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
                                color = MaterialTheme.colorScheme.secondary
                            )
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val dateStartText = dateStart?.let { millis ->
                            SimpleDateFormat(
                                DateAndTimeFormat.DATE_FORMAT,
                                Locale.getDefault()
                            ).format(
                                millis
                            )
                        } ?: DEFAULT_DATE_TEXT

                        Text(
                            text = dateStartText,
                            style = Typography.bodyLarge,
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
                                style = Typography.bodyLarge,
                                color = setTextColor(dateStart)
                            )
                        }
                    }

                    Column(
                        modifier = Modifier
                            .border(
                                width = 0.5.dp,
                                shape = ShapeBackground.small,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            .padding(18.dp)
                            .clickable {
                                endDatePicker.show()
                            },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val dateStartText = dateEnd?.let { millis ->
                            SimpleDateFormat(
                                DateAndTimeFormat.DATE_FORMAT,
                                Locale.getDefault()
                            ).format(
                                millis
                            )
                        } ?: DEFAULT_DATE_TEXT

                        Text(
                            text = dateStartText,
                            style = Typography.bodyLarge,
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
                                style = Typography.bodyLarge,
                                color = setTextColor(dateEnd)
                            )
                        }
                    }
                }

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
                        style = Typography.bodyMedium.copy(
                            color = if (viewModel.restState) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onPrimary
                            }
                        )
                    )

                    Switch(
                        checked = viewModel.restState,
                        onCheckedChange = {
                            viewModel.setRest(it)
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
                    val text =
                        stringResource(id = R.string.info_text_min_time_rest, minTimeRestText)

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

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .constrainAs(info) {
                            start.linkTo(parent.start)
                            top.linkTo(restSwitch.bottom)
                        },
                ) {
                    AnimatedVisibility(
                        visible = viewModel.restState,
                        enter = slideInHorizontally(animationSpec = tween(durationMillis = 300))
                                + fadeIn(animationSpec = tween(durationMillis = 300)),
                        exit = slideOutHorizontally(animationSpec = tween(durationMillis = 300))
                                + fadeOut(animationSpec = tween(durationMillis = 150))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f)),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Icon(
                                modifier = Modifier.padding(
                                    top = 16.dp,
                                    start = 16.dp,
                                    bottom = 8.dp
                                ),
                                painter = painterResource(id = R.drawable.ic_info_24),
                                tint = MaterialTheme.colorScheme.tertiary,
                                contentDescription = null
                            )
                            minRest?.let {
                                SimpleDateFormat(
                                    "${DateAndTimeFormat.DATE_FORMAT} ${DateAndTimeFormat.TIME_FORMAT}",
                                    Locale.getDefault()
                                ).format(
                                    it
                                )
                            }
                                ?.also {
                                    Text(
                                        modifier = Modifier.padding(horizontal = 16.dp),
                                        text = stringResource(id = R.string.min_time_rest_text, it),
                                        style = Typography.bodyMedium,
                                        textAlign = TextAlign.End
                                    )
                                }

                            completeRest?.let {
                                SimpleDateFormat(
                                    "${DateAndTimeFormat.DATE_FORMAT} ${DateAndTimeFormat.TIME_FORMAT}",
                                    Locale.getDefault()
                                ).format(
                                    it
                                )
                            }
                                ?.also {
                                    Text(
                                        modifier = Modifier.padding(horizontal = 16.dp),
                                        text = stringResource(
                                            id = R.string.complete_time_rest_text,
                                            it
                                        ),
                                        style = Typography.bodyMedium,
                                        textAlign = TextAlign.End
                                    )
                                }

                            ClickableText(
                                modifier = Modifier.padding(
                                    start = 16.dp,
                                    bottom = 16.dp,
                                    end = 16.dp,
                                    top = 12.dp
                                ),
                                text = link,
                                style = Typography.bodySmall
                                    .copy(
                                        fontStyle = FontStyle.Italic,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                            ) {
                                link.getStringAnnotations(LINK_TO_SETTING, it, it)
                                    .firstOrNull()?.let { stringAnnotation ->
                                        navController.navigate(stringAnnotation.item)
                                    }
                            }
                        }
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp, horizontal = 24.dp),
                        hint = "№ маршрута",
                        value = number,
                        onValueChange = { viewModel.setNumber(it) }
                    )
                    HorizontalDividerTrainDriver(modifier = Modifier.padding(horizontal = 24.dp))
                    Spacer(modifier = Modifier.height(24.dp))
                    ItemAddLoco(
                        openSheet,
                        viewModel.stateLocoList,
                        viewModel::deleteLocomotiveInRoute
                    )
                    HorizontalDividerTrainDriver(modifier = Modifier.padding(horizontal = 24.dp))
//                    ItemAddLoco(openSheet, viewModel.stateLocoList.value)
//                    HorizontalDividerTrainDriver(modifier = Modifier.padding(horizontal = 24.dp))
//                    ItemAddLoco(openSheet, viewModel.stateLocoList.value)
                }
            }
        }
    }
}

@Composable
fun SheetLayout(sheet: BottomSheetScreen, closeSheet: () -> Unit, viewModel: AddingViewModel) {
    BottomSheetWithCloseDialog(
        modifier = Modifier.fillMaxHeight(0.96f), closeSheet = closeSheet
    ) {
        when (sheet) {
            is BottomSheetScreen.AddingLoco -> AddingLocoScreen(
                timeState = viewModel.timeEditState,
                locomotive = sheet.locomotive,
                closeAddingLocoScreen = closeSheet,
                stateLocomotiveList = viewModel.stateLocoList
            )
            is BottomSheetScreen.AddingTrain -> AddingTrainScreen()
            is BottomSheetScreen.AddingPass -> AddingPassScreen()
        }
    }
}

@Composable
@OptIn(ExperimentalComposeUiApi::class)
fun NumberEditItem(
    modifier: Modifier = Modifier,
    value: TextFieldValue,
    hint: String? = null,
    onValueChange: (TextFieldValue) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    BasicTextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        textStyle = Typography.titleMedium.copy(color = MaterialTheme.colorScheme.primary),
        decorationBox = { innerTextField ->
            Box(
                contentAlignment = Alignment.CenterStart
            ) {
                if (value.text.isEmpty()) {
                    hint?.let {
                        Text(
                            text = it,
                            style = Typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
                innerTextField()
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                keyboardController?.hide()
                focusManager.clearFocus()
            }
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemAddLoco(
    openSheet: (BottomSheetScreen) -> Unit,
    locoList: List<Locomotive>,
    deleteLoco: (Locomotive) -> Unit
) {
    val scope = rememberCoroutineScope()
    Card {
        ConstraintLayout(
            modifier = Modifier
                .clickable {
                    scope.launch {
                        val locoScreen = BottomSheetScreen.AddingLoco
                        locoScreen.locomotive = null
                        openSheet.invoke(locoScreen)
                    }
                }
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
                style = Typography.titleMedium.copy(color = MaterialTheme.colorScheme.primary)
            )
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .fillMaxWidth()
                    .constrainAs(data) {
                        start.linkTo(parent.start)
                        bottom.linkTo(parent.bottom)
                        end.linkTo(icon.start, 8.dp)
                        width = Dimension.fillToConstraints
                    },
            ) {
                locoList.forEach { locomotive ->
                    AssistChip(
                        modifier = Modifier
                            .padding(end = 16.dp),
                        onClick = {
                            scope.launch {
                                val locoScreen = BottomSheetScreen.AddingLoco
                                locoScreen.locomotive = locomotive
                                openSheet.invoke(locoScreen)
                            }
                        },
                        shape = ShapeBackground.small,
                        label = {
                            Text(
                                text = "${locomotive.series} №${locomotive.number}",
                                style = Typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onPrimary)
                            )
                        },
                        trailingIcon = {
                            Icon(
                                modifier = Modifier
                                    .padding(start = 4.dp)
                                    .clickable {
                                        deleteLoco.invoke(locomotive)
                                    },
                                painter = painterResource(id = R.drawable.ic_close_24),
                                contentDescription = null
                            )
                        }
                    )
                }
                Image(
                    modifier = Modifier
                        .size(20.dp),
//                        .constrainAs(icon) {
//                            end.linkTo(parent.end)
//                            bottom.linkTo(parent.bottom)
//                            top.linkTo(parent.top)
//                        },
                    painter = painterResource(id = R.drawable.ic_forward_24),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.secondary)
                )
            }
        }
    }
}
//
//    @ExperimentalMaterial3Api
//    @Composable
//    private fun AddingAppBar(navController: NavController, enabled: Boolean) {
//        TopAppBar(
//            modifier = Modifier
//                .fillMaxHeight(0.12f),
//        ) {
//            ConstraintLayout(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(start = 12.dp, end = 16.dp, bottom = 6.dp)
//            ) {
//                val (icon, button) = createRefs()
//                IconButton(
//                    modifier = Modifier
//                        .size(dimensionResource(id = R.dimen.min_size_view))
//                        .constrainAs(icon) {
//                            start.linkTo(parent.start)
//                            bottom.linkTo(parent.bottom)
//                        },
//                    onClick = {
//                        navController.navigateUp()
//                    }
//                ) {
//                    Image(
//                        modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size)),
//                        painter = painterResource(id = R.drawable.ic_back_24),
//                        contentDescription = null,
//                        colorFilter = ColorFilter.tint(MaterialTheme.colors.primary)
//                    )
//                }
//
//                val linkText = buildAnnotatedString {
//                    val text = "Сохранить"
//                    val start = 0
//                    val end = text.length
//                    append(text)
//
//                    addStringAnnotation(
//                        tag = LINK_TO_HOME,
//                        annotation = Screen.Home.route,
//                        start = start,
//                        end = end
//                    )
//                }
//
//                ClickableTextTrainDriver(
//                    modifier = Modifier.constrainAs(button) {
//                        end.linkTo(parent.end)
//                        top.linkTo(icon.top)
//                        bottom.linkTo(icon.bottom)
//                    },
//                    enabled = enabled,
//                    text = linkText,
//                    style = Typography.button
//                ) {
//                    // TODO SAVE TO REPOSITORY
//                    linkText.getStringAnnotations(LINK_TO_HOME, it, it)
//                        .firstOrNull()?.let { stringAnnotation ->
//                            navController.navigate(stringAnnotation.item)
//                        }
//                }
//            }
//        }
//    }
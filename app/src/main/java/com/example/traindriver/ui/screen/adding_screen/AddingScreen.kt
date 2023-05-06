package com.example.traindriver.ui.screen.adding_screen

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.material3.AssistChip
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
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
import androidx.navigation.NavController
import com.example.traindriver.R
import com.example.traindriver.domain.entity.Locomotive
import com.example.traindriver.ui.element_screen.HorizontalDividerTrainDriver
import com.example.traindriver.ui.screen.Screen
import com.example.traindriver.ui.screen.adding_screen.bottom_sheet_screen.*
import com.example.traindriver.ui.screen.adding_screen.state_holder.WorkTimeEvent
import com.example.traindriver.ui.screen.adding_screen.state_holder.WorkTimeType
import com.example.traindriver.ui.screen.viewing_route_screen.element.LINK_TO_SETTING
import com.example.traindriver.ui.screen.viewing_route_screen.element.setTextColor
import com.example.traindriver.ui.screen.viewing_route_screen.element.startIndexLastWord
import com.example.traindriver.ui.theme.ShapeBackground
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
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.DpOffset
import com.example.traindriver.data.util.ResultState
import com.example.traindriver.domain.entity.Train
import com.maxkeppeker.sheets.core.models.base.Header
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.maxkeppeler.sheets.clock.ClockDialog
import com.maxkeppeler.sheets.clock.models.ClockConfig
import com.maxkeppeler.sheets.clock.models.ClockSelection
import java.time.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
fun AddingScreen(
    navController: NavController,
    uid: String,
    viewModel: AddingViewModel
) {
    OnLifecycleEvent { _, event ->
        when (event) {
            Lifecycle.Event.ON_CREATE -> {
                viewModel.getMinTimeRest()
                viewModel.setData(uid)
            }
            else -> {}
        }
    }

    BackHandler {
        navController.navigateUp()
        viewModel.clearField()
    }

    SavesStateHomeScreen(viewModel, navController)

    val number = viewModel.numberRouteState

    val timeValue = viewModel.timeEditState.value

    val dateStart = timeValue.startTime.time
    val dateEnd = timeValue.endTime.time

    val timeResult = dateEnd - dateStart

    val calendarStartState = rememberUseCaseState()
    val timeStartState = rememberUseCaseState()

    val dateStartLocalDateTime: LocalDateTime? =
        dateStart?.let {
            LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault())
        }

    var localDateStart by remember {
        mutableStateOf<LocalDate?>(null)
    }
    var localTimeStart by remember {
        mutableStateOf<LocalTime?>(null)
    }
    var dateAndTimeStartWork by remember {
        mutableStateOf<LocalDateTime?>(null)
    }

    CalendarDialog(
        state = calendarStartState,
        selection = CalendarSelection.Date(
            selectedDate = dateStartLocalDateTime?.toLocalDate()
        ) { date ->
            localDateStart = LocalDate.of(date.year, date.month, date.dayOfMonth)
            timeStartState.show()
        },
        header = Header.Default(
            title = "Начало работы"
        ),
        config = CalendarConfig(
            monthSelection = true,
            yearSelection = true,
        ),
    )

    ClockDialog(
        state = timeStartState,
        selection = ClockSelection.HoursMinutes { hours, minutes ->
            localTimeStart = LocalTime.of(hours, minutes)
            dateAndTimeStartWork = LocalDateTime.of(localDateStart, localTimeStart)
            val dateInLong = dateAndTimeStartWork!!.toLong()
            viewModel.createEvent(WorkTimeEvent.EnteredStartTime(dateInLong))
            viewModel.createEvent(WorkTimeEvent.FocusChange(WorkTimeType.START))
        },
        header = Header.Default(
            title = "Начало работы"
        ),
        config = ClockConfig(
            is24HourFormat = true,
        )
    )

    val calendarEndState = rememberUseCaseState()
    val timeEndState = rememberUseCaseState()

    val dateEndLocalDateTime: LocalDateTime? =
        dateStart?.let {
            LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault())
        }

    var localDateEnd by remember {
        mutableStateOf<LocalDate?>(null)
    }
    var localTimeEnd by remember {
        mutableStateOf<LocalTime?>(null)
    }
    var dateAndTimeEndWork by remember {
        mutableStateOf<LocalDateTime?>(null)
    }

    CalendarDialog(
        state = calendarEndState,
        selection = CalendarSelection.Date(
            selectedDate = dateEndLocalDateTime?.toLocalDate()
        ) { date ->
            localDateEnd = LocalDate.of(date.year, date.month, date.dayOfMonth)
            timeEndState.show()
        },
        header = Header.Default(
            title = "Окончание работы"
        ),
        config = CalendarConfig(
            monthSelection = true,
            yearSelection = true,
        ),
    )

    ClockDialog(
        state = timeEndState,
        selection = ClockSelection.HoursMinutes { hours, minutes ->
            localTimeEnd = LocalTime.of(hours, minutes)
            dateAndTimeEndWork = LocalDateTime.of(localDateEnd, localTimeEnd)
            val dateInLong = dateAndTimeEndWork!!.toLong()
            viewModel.createEvent(WorkTimeEvent.EnteredEndTime(dateInLong))
            viewModel.createEvent(WorkTimeEvent.FocusChange(WorkTimeType.END))
        },
        header = Header.Default(
            title = "Окончание работы"
        ),
        config = ClockConfig(
            is24HourFormat = true,
        )
    )
    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = {
                    Text(
                        text = "Маршрут",
                        style = Typography.headlineSmall
                            .copy(color = MaterialTheme.colorScheme.primary)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigateUp()
                        viewModel.clearField()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                },
                actions = {
                    ClickableText(
                        text = AnnotatedString(text = "Сохранить"),
                        style = Typography.titleMedium,
                        onClick = {
                            viewModel.addRouteInRepository()
                        }
                    )
                    var dropDownExpanded by remember { mutableStateOf(false) }

                    IconButton(
                        onClick = {
                            dropDownExpanded = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Меню"
                        )
                        DropdownMenu(
                            expanded = dropDownExpanded,
                            onDismissRequest = { dropDownExpanded = false },
                            offset = DpOffset(x = 4.dp, y = 8.dp)
                        ) {
                            DropdownMenuItem(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                onClick = {
                                    viewModel.clearField()
                                    dropDownExpanded = false
                                },
                                text = {
                                    Text(
                                        text = "Очистить",
                                        style = Typography.bodyLarge.copy(color = MaterialTheme.colorScheme.primary)
                                    )
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    navigationIconContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
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
                        imageVector = Icons.Default.ErrorOutline,
                        tint = MaterialTheme.colorScheme.error,
                        contentDescription = "Ошибка"
                    )
                    Text(
                        modifier = Modifier.padding(start = 8.dp),
                        text = timeValue.errorMessage,
                        style = Typography.bodyLarge.copy(color = MaterialTheme.colorScheme.error),
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
                            calendarStartState.show()
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
                            calendarEndState.show()
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
                            MaterialTheme.colorScheme.secondary
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
            val minRest: Long? = if (viewModel.timeEditState.value.formValid) {
                halfRest?.let { half ->
                    if (half > viewModel.minTimeRest) {
                        dateEnd + half
                    } else {
                        dateEnd + viewModel.minTimeRest
                    }
                }
            } else {
                null
            }
            val completeRest: Long? = if (viewModel.timeEditState.value.formValid) {
                dateEnd + timeResult
            } else {
                null
            }
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
                        .padding(vertical = 12.dp, horizontal = 16.dp),
                    prefix = "№",
                    placeholder = "маршрута",
                    value = number,
                    onValueChange = { viewModel.setNumber(it) }
                )
                HorizontalDividerTrainDriver(modifier = Modifier.padding(horizontal = 24.dp))
                Spacer(modifier = Modifier.height(24.dp))
                ItemAddLoco(
                    navController,
                    viewModel.stateLocoList,
                    viewModel::deleteLocomotiveInRoute
                )
                Spacer(modifier = Modifier.height(24.dp))
                ItemAddTrain(
                    navController,
                    viewModel.stateTrainList,
                    viewModel::deleteTrainInRoute
                )
//                    ItemAddLoco(openSheet, viewModel.stateLocoList.value)
            }
        }
    }
}

@Composable
fun ItemAddTrain(
    navController: NavController,
    trainList: SnapshotStateList<Train>,
    deleteTrain: (Train) -> Unit
) {
    val scope = rememberCoroutineScope()
    Card(modifier = Modifier.padding(horizontal = 16.dp)) {
        ConstraintLayout(
            modifier = Modifier
                .clickable {
                    scope.launch {
                        navController.navigate(Screen.AddingTrain.route)
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
                text = "Поезд",
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
                trainList.forEachIndexed { index, train ->
                    AssistChip(
                        modifier = Modifier
                            .padding(end = 16.dp),
                        onClick = {
                            scope.launch {
                                navController.navigate(
                                    Screen.AddingTrain.setId(train.id)
                                )
                            }
                        },
                        shape = ShapeBackground.small,
                        label = {
                            if (train.number.isNullOrBlank()) {
                                Text(text = "Поезд №${index + 1}")
                            } else {
                                Text(
                                    text = "№${train.number}",
                                    style = Typography.bodyMedium.copy(color = MaterialTheme.colorScheme.primary)
                                )
                            }
                        },
                        trailingIcon = {
                            Icon(
                                modifier = Modifier
                                    .padding(start = 4.dp)
                                    .clickable {
                                        deleteTrain.invoke(train)
                                    },
                                painter = painterResource(id = R.drawable.ic_close_24),
                                contentDescription = null
                            )
                        }
                    )
                }
            }
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
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.secondary)
            )
        }
    }
}

@Composable
@OptIn(ExperimentalComposeUiApi::class)
fun NumberEditItem(
    modifier: Modifier = Modifier,
    value: TextFieldValue,
    placeholder: String? = null,
    prefix: String? = null,
    onValueChange: (TextFieldValue) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    OutlinedTextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            placeholder?.let {
                Text(text = it, color = MaterialTheme.colorScheme.secondary)
            }
        },
        prefix = {
            prefix?.let {
                Text(text = prefix, color = MaterialTheme.colorScheme.secondary)
            }
        },
        textStyle = Typography.titleMedium.copy(color = MaterialTheme.colorScheme.primary),

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

@Composable
fun ItemAddLoco(
    navController: NavController,
    locoList: List<Locomotive>,
    deleteLoco: (Locomotive) -> Unit
) {
    val scope = rememberCoroutineScope()
    Card(modifier = Modifier.padding(horizontal = 16.dp)) {
        ConstraintLayout(
            modifier = Modifier
                .clickable {
                    scope.launch {
                        navController.navigate(Screen.AddingLoco.route)
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
                locoList.forEachIndexed { index, locomotive ->
                    AssistChip(
                        modifier = Modifier
                            .padding(end = 16.dp),
                        onClick = {
                            scope.launch {
                                navController.navigate(
                                    Screen.AddingLoco.setId(locomotive.id)
                                )
                            }
                        },
                        shape = ShapeBackground.small,
                        label = {
                            if (locomotive.series.isNullOrBlank() && locomotive.number.isNullOrBlank()) {
                                Text(text = "Локомотив №${index + 1}")
                            } else {
                                Text(
                                    text = "${locomotive.series} №${locomotive.number}",
                                    style = Typography.bodyMedium.copy(color = MaterialTheme.colorScheme.primary)
                                )
                            }
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
            }
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
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.secondary)
            )
        }
    }
}

@Composable
fun SavesStateHomeScreen(viewModel: AddingViewModel, navController: NavController) {
    when (val state = viewModel.savesState) {
        is ResultState.Loading -> {

        }
        is ResultState.Success -> {
            if (state == ResultState.Success(true)) {
                viewModel.savesState = ResultState.Success(false)
                navController.navigate(Screen.Home.route)
                viewModel.clearField()
            }
        }
        is ResultState.Failure -> {

        }
    }
}
package com.example.traindriver.ui.screen.adding_screen.adding_passenger

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.traindriver.ui.screen.adding_screen.AddingViewModel
import com.example.traindriver.ui.screen.adding_screen.toLong
import com.example.traindriver.ui.screen.viewing_route_screen.element.BottomShadow
import com.example.traindriver.ui.screen.viewing_route_screen.element.isScrollInInitialState
import com.example.traindriver.ui.screen.viewing_route_screen.element.setTextColor
import com.example.traindriver.ui.theme.ShapeBackground
import com.example.traindriver.ui.theme.Typography
import com.example.traindriver.ui.util.DateAndTimeFormat
import com.example.traindriver.ui.util.OnLifecycleEvent
import com.example.traindriver.ui.util.long_util.getTimeInStringFormat
import com.maxkeppeker.sheets.core.models.base.Header
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.maxkeppeler.sheets.clock.ClockDialog
import com.maxkeppeler.sheets.clock.models.ClockConfig
import com.maxkeppeler.sheets.clock.models.ClockSelection
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.*
import java.util.*
import com.example.traindriver.ui.util.long_util.minus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddingPassengerScreen(
    navController: NavController,
    passengerId: String?,
    addingRouteViewModel: AddingViewModel
) {
    val addingPassengerViewModel: AddingPassengerViewModel = viewModel()
    val scope = rememberCoroutineScope()

    OnLifecycleEvent { _, event ->
        when (event) {
            Lifecycle.Event.ON_CREATE -> {
                addingPassengerViewModel.setData(
                    passenger = addingRouteViewModel.statePassengerList.find { passenger ->
                        passenger.id == passengerId
                    }
                )
            }
            else -> {}
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxWidth(),
        topBar = {
            MediumTopAppBar(
                title = {
                    Text(
                        text = "Пассажиром",
                        style = Typography.headlineSmall
                            .copy(color = MaterialTheme.colorScheme.primary)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigateUp()
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
                            addingPassengerViewModel.addPassengerInRoute(
                                addingRouteViewModel.statePassengerList
                            )
                            navController.navigateUp()
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
                                    addingPassengerViewModel.clearField()
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
                .fillMaxWidth()
                .padding(paddingValues)
        ) {
            val (topShadow, lazyColumn) = createRefs()
            val scrollState = rememberLazyListState()

            val formState = addingPassengerViewModel.formState
            val numberTrain = formState.numberTrain.data
            val stationDeparture = formState.stationDeparture.data
            val stationArrival = formState.stationArrival.data
            val timeDeparture = formState.timeDeparture.data
            val timeArrival = formState.timeArrival.data
            val notes = formState.notes.data

            AnimatedVisibility(
                modifier = Modifier
                    .zIndex(1f)
                    .constrainAs(topShadow) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                    },
                visible = !scrollState.isScrollInInitialState(),
                enter = fadeIn(animationSpec = tween(durationMillis = 300)),
                exit = fadeOut(animationSpec = tween(durationMillis = 300))
            ) {
                BottomShadow()
            }

            LazyColumn(
                modifier = Modifier
                    .constrainAs(lazyColumn) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                    },
                state = scrollState,
                horizontalAlignment = Alignment.End,
                contentPadding = PaddingValues(horizontal = 24.dp)
            ) {
                item {
                    val arrival = addingPassengerViewModel.formState.timeArrival.data
                    val departure = addingPassengerViewModel.formState.timeDeparture.data
                    val timeResult = arrival - departure

                    Spacer(modifier = Modifier.padding(20.dp))
                    if (!formState.formValid.data) {
                        AnimatedVisibility(
                            visible = !formState.formValid.data,
                            enter = slideInVertically(animationSpec = tween(durationMillis = 500))
                                    + fadeIn(animationSpec = tween(durationMillis = 300)),
                            exit = slideOutVertically(animationSpec = tween(durationMillis = 500))
                                    + fadeOut(animationSpec = tween(durationMillis = 150))
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ErrorOutline,
                                    tint = MaterialTheme.colorScheme.error,
                                    contentDescription = "Ошибка"
                                )
                                Text(
                                    modifier = Modifier.padding(start = 8.dp),
                                    text = formState.errorMessage,
                                    style = Typography.bodyLarge.copy(color = MaterialTheme.colorScheme.error),
                                )
                            }
                        }
                    } else {
                        AnimatedVisibility(
                            visible = timeResult != null && timeResult > 0,
                            enter = slideInVertically(animationSpec = tween(durationMillis = 500))
                                    + fadeIn(animationSpec = tween(durationMillis = 300)),
                            exit = slideOutVertically(animationSpec = tween(durationMillis = 500))
                                    + fadeOut(animationSpec = tween(durationMillis = 150))
                        ) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = timeResult.getTimeInStringFormat(),
                                style = Typography.headlineLarge.copy(
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                    }
                }
                item {
                    val focusManager = LocalFocusManager.current
                    Row(
                        modifier = Modifier.padding(top = 24.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedTextField(
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .fillMaxWidth(0.4f),
                            value = numberTrain ?: "",
                            onValueChange = {
                                addingPassengerViewModel.createEventPassenger(
                                    PassengerEvent.EnteredTrainNumber(it)
                                )
                            },
                            textStyle = Typography.bodyLarge
                                .copy(color = MaterialTheme.colorScheme.primary),
                            label = {
                                Text(
                                    text = "№ поезда",
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = {
                                    scope.launch {
                                        focusManager.clearFocus()
                                    }
                                }
                            ),
                            singleLine = true
                        )
                    }
                }
                item {
                    val focusManager = LocalFocusManager.current
                    ConstraintLayout(
                        modifier = Modifier
                            .padding(top = 12.dp)
                            .fillMaxWidth(),
                    ) {
                        val (stationField, timeField) = createRefs()

                        OutlinedTextField(
                            modifier = Modifier
                                .constrainAs(stationField) {
                                    start.linkTo(parent.start)
                                    top.linkTo(parent.top)
                                    end.linkTo(timeField.start)
                                    bottom.linkTo(parent.bottom)
                                    width = Dimension.percent(0.6f)
                                }
                                .padding(end = 8.dp),
                            value = stationDeparture ?: "",
                            onValueChange = {
                                addingPassengerViewModel.createEventPassenger(
                                    PassengerEvent.EnteredStationDeparture(it)
                                )
                            },
                            textStyle = Typography.bodyLarge
                                .copy(color = MaterialTheme.colorScheme.primary),
                            label = {
                                Text(
                                    text = "Ст. отправления",
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = {
                                    scope.launch {
                                        focusManager.clearFocus()
                                    }
                                }
                            ),
                            singleLine = true,
                        )

                        val calendarDepartureState = rememberUseCaseState()
                        val clockDepartureState = rememberUseCaseState()

                        val departureToLocalDateTime: LocalDateTime? =
                            timeDeparture?.let {
                                LocalDateTime.ofInstant(
                                    Instant.ofEpochMilli(it),
                                    ZoneId.systemDefault()
                                )
                            }

                        var localDateDeparture by remember {
                            mutableStateOf<LocalDate?>(null)
                        }
                        var localTimeDeparture by remember {
                            mutableStateOf<LocalTime?>(null)
                        }
                        var dateAndTimeDeparture by remember {
                            mutableStateOf<LocalDateTime?>(null)
                        }

                        CalendarDialog(
                            state = calendarDepartureState,
                            selection = CalendarSelection.Date(
                                selectedDate = departureToLocalDateTime?.toLocalDate()
                            ) { date ->
                                localDateDeparture =
                                    LocalDate.of(date.year, date.month, date.dayOfMonth)
                                clockDepartureState.show()
                            },
                            header = Header.Default(
                                title = "Отправление"
                            ),
                            config = CalendarConfig(
                                monthSelection = true,
                                yearSelection = true,
                            ),
                        )

                        ClockDialog(
                            state = clockDepartureState,
                            selection = ClockSelection.HoursMinutes { hours, minutes ->
                                localTimeDeparture = LocalTime.of(hours, minutes)
                                dateAndTimeDeparture =
                                    LocalDateTime.of(localDateDeparture, localTimeDeparture)
                                val dateInLong = dateAndTimeDeparture!!.toLong()
                                addingPassengerViewModel.createEventPassenger(
                                    PassengerEvent.EnteredTimeDeparture(data = dateInLong)
                                )
                                addingPassengerViewModel.createEventPassenger(
                                    PassengerEvent.FocusChange(fieldName = PassengerDataType.TIME_DEPARTURE)
                                )
                            },
                            header = Header.Default(
                                title = "Отправление"
                            ),
                            config = ClockConfig(
                                is24HourFormat = true,
                            )
                        )

                        Column(
                            modifier = Modifier
                                .constrainAs(timeField) {
                                    start.linkTo(stationField.end)
                                    top.linkTo(stationField.top)
                                    end.linkTo(parent.end)
                                    bottom.linkTo(stationField.bottom)
                                    width = Dimension.percent(0.4f)
                                    height = Dimension.fillToConstraints
                                }
                                .padding(end = 8.dp, top = 8.dp)
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outline,
                                    shape = ShapeBackground.extraSmall
                                )
                                .background(
                                    color = if (!formState.formValid.data
                                        && formState.formValid.type == PassengerDataType.TIME_DEPARTURE
                                    ) {
                                        MaterialTheme.colorScheme.errorContainer
                                    } else {
                                        Color.Transparent
                                    }
                                )
                                .clickable {
                                    calendarDepartureState.show()
                                },
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            val textDateDeparture =
                                formState.timeDeparture.data?.let { millis ->
                                    SimpleDateFormat(
                                        DateAndTimeFormat.DATE_FORMAT,
                                        Locale.getDefault()
                                    ).format(
                                        millis
                                    )
                                } ?: DateAndTimeFormat.DEFAULT_DATE_TEXT

                            val textTimeDeparture =
                                formState.timeDeparture.data?.let { millis ->
                                    SimpleDateFormat(
                                        DateAndTimeFormat.TIME_FORMAT,
                                        Locale.getDefault()
                                    ).format(
                                        millis
                                    )
                                } ?: DateAndTimeFormat.DEFAULT_TIME_TEXT

                            Text(
                                text = textDateDeparture,
                                color = setTextColor(any = formState.timeDeparture.data)
                            )
                            Text(
                                text = textTimeDeparture,
                                color = setTextColor(any = formState.timeDeparture.data)
                            )
                        }
                    }
                }
                item {
                    val focusManager = LocalFocusManager.current
                    ConstraintLayout(
                        modifier = Modifier
                            .padding(top = 12.dp)
                            .fillMaxWidth(),
                    ) {
                        val (stationField, timeField) = createRefs()

                        OutlinedTextField(
                            modifier = Modifier
                                .constrainAs(stationField) {
                                    start.linkTo(parent.start)
                                    top.linkTo(parent.top)
                                    end.linkTo(timeField.start)
                                    bottom.linkTo(parent.bottom)
                                    width = Dimension.percent(0.6f)
                                }
                                .padding(end = 8.dp),
                            value = stationArrival ?: "",
                            onValueChange = {
                                addingPassengerViewModel.createEventPassenger(
                                    PassengerEvent.EnteredStationArrival(it)
                                )
                            },
                            textStyle = Typography.bodyLarge
                                .copy(color = MaterialTheme.colorScheme.primary),
                            label = {
                                Text(
                                    text = "Ст. прибытия",
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = {
                                    scope.launch {
                                        focusManager.clearFocus()
                                    }
                                }
                            ),
                            singleLine = true,
                        )

                        val calendarArrivalState = rememberUseCaseState()
                        val clockArrivalState = rememberUseCaseState()

                        val arrivalToLocalDateTime: LocalDateTime? =
                            timeArrival?.let {
                                LocalDateTime.ofInstant(
                                    Instant.ofEpochMilli(it),
                                    ZoneId.systemDefault()
                                )
                            }

                        var localDateArrival by remember {
                            mutableStateOf<LocalDate?>(null)
                        }
                        var localTimeArrival by remember {
                            mutableStateOf<LocalTime?>(null)
                        }
                        var dateAndTimeArrival by remember {
                            mutableStateOf<LocalDateTime?>(null)
                        }

                        CalendarDialog(
                            state = calendarArrivalState,
                            selection = CalendarSelection.Date(
                                selectedDate = arrivalToLocalDateTime?.toLocalDate()
                            ) { date ->
                                localDateArrival =
                                    LocalDate.of(date.year, date.month, date.dayOfMonth)
                                clockArrivalState.show()
                            },
                            header = Header.Default(
                                title = "Прибытие"
                            ),
                            config = CalendarConfig(
                                monthSelection = true,
                                yearSelection = true,
                            ),
                        )

                        ClockDialog(
                            state = clockArrivalState,
                            selection = ClockSelection.HoursMinutes { hours, minutes ->
                                localTimeArrival = LocalTime.of(hours, minutes)
                                dateAndTimeArrival =
                                    LocalDateTime.of(localDateArrival, localTimeArrival)
                                val dateInLong = dateAndTimeArrival!!.toLong()
                                addingPassengerViewModel.createEventPassenger(
                                    PassengerEvent.EnteredTimeArrival(data = dateInLong)
                                )
                                addingPassengerViewModel.createEventPassenger(
                                    PassengerEvent.FocusChange(fieldName = PassengerDataType.TIME_ARRIVAL)
                                )
                            },
                            header = Header.Default(
                                title = "Отправление"
                            ),
                            config = ClockConfig(
                                is24HourFormat = true,
                            )
                        )

                        Column(
                            modifier = Modifier
                                .constrainAs(timeField) {
                                    start.linkTo(stationField.end)
                                    top.linkTo(stationField.top)
                                    end.linkTo(parent.end)
                                    bottom.linkTo(stationField.bottom)
                                    width = Dimension.percent(0.4f)
                                    height = Dimension.fillToConstraints
                                }
                                .padding(end = 8.dp, top = 8.dp)
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outline,
                                    shape = ShapeBackground.extraSmall
                                )
                                .background(
                                    color = if (!formState.formValid.data
                                        && formState.formValid.type == PassengerDataType.TIME_ARRIVAL
                                    ) {
                                        MaterialTheme.colorScheme.errorContainer
                                    } else {
                                        Color.Transparent
                                    }
                                )
                                .clickable {
                                    calendarArrivalState.show()
                                },
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            val textDateDeparture = formState.timeArrival.data?.let { millis ->
                                SimpleDateFormat(
                                    DateAndTimeFormat.DATE_FORMAT,
                                    Locale.getDefault()
                                ).format(
                                    millis
                                )
                            } ?: DateAndTimeFormat.DEFAULT_DATE_TEXT

                            val textTimeDeparture = formState.timeArrival.data?.let { millis ->
                                SimpleDateFormat(
                                    DateAndTimeFormat.TIME_FORMAT,
                                    Locale.getDefault()
                                ).format(
                                    millis
                                )
                            } ?: DateAndTimeFormat.DEFAULT_TIME_TEXT

                            Text(
                                text = textDateDeparture,
                                color = setTextColor(any = formState.timeArrival.data)
                            )
                            Text(
                                text = textTimeDeparture,
                                color = setTextColor(any = formState.timeArrival.data)
                            )
                        }
                    }
                }
                item {
                    val focusManager = LocalFocusManager.current
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 8.dp, top = 12.dp),
                        value = notes ?: "",
                        onValueChange = {
                            addingPassengerViewModel.createEventPassenger(
                                PassengerEvent.EnteredNotes(it)
                            )
                        },
                        textStyle = Typography.bodyLarge
                            .copy(color = MaterialTheme.colorScheme.primary),
                        label = {
                            Text(
                                text = "Примечания",
                                color = MaterialTheme.colorScheme.secondary
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = {
                                scope.launch {
                                    focusManager.clearFocus()
                                }
                            }
                        ),
                    )
                }
            }
        }
    }
}
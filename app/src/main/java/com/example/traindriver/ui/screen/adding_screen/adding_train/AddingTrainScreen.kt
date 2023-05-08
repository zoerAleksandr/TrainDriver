package com.example.traindriver.ui.screen.adding_screen.adding_train

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.traindriver.domain.entity.Station
import com.example.traindriver.ui.screen.Screen
import com.example.traindriver.ui.screen.adding_screen.AddingViewModel
import com.example.traindriver.ui.screen.adding_screen.adding_loco.ActionsRow
import com.example.traindriver.ui.screen.adding_screen.adding_loco.DraggableItem
import com.example.traindriver.ui.screen.adding_screen.toLong
import com.example.traindriver.ui.screen.signin_screen.elements.SecondarySpacer
import com.example.traindriver.ui.screen.viewing_route_screen.element.BottomShadow
import com.example.traindriver.ui.screen.viewing_route_screen.element.isScrollInInitialState
import com.example.traindriver.ui.screen.viewing_route_screen.element.setTextColor
import com.example.traindriver.ui.theme.ShapeBackground
import com.example.traindriver.ui.theme.Typography
import com.example.traindriver.ui.util.DateAndTimeFormat
import com.example.traindriver.ui.util.OnLifecycleEvent
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddingTrainScreen(
    navController: NavController,
    trainId: String? = null,
    addingRouteViewModel: AddingViewModel,
) {
    val addingTrainViewModel: AddTrainViewModel = viewModel()
    val scope = rememberCoroutineScope()

    OnLifecycleEvent { _, event ->
        when (event) {
            Lifecycle.Event.ON_CREATE -> {
                addingTrainViewModel.setData(
                    train = addingRouteViewModel.stateTrainList.find { train ->
                        train.id == trainId
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
                        text = "Поезд",
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
                            addingTrainViewModel.addTrainInRoute(
                                addingRouteViewModel.stateTrainList
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
                                    addingTrainViewModel.clearField()
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

            val numberTrain = addingTrainViewModel.numberTrain
            val weightTrain = addingTrainViewModel.weightTrain
            val axleTrain = addingTrainViewModel.axleTrain
            val conditionalLengthTrain = addingTrainViewModel.conditionalLengthTrain
            val locomotiveTrain = addingTrainViewModel.locomotiveTrain
            val stationListTrain = addingTrainViewModel.stationListState

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
                    val focusManager = LocalFocusManager.current
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp)
                    ) {
                        OutlinedTextField(
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .weight(0.5f),
                            value = numberTrain,
                            onValueChange = {
                                addingTrainViewModel.setTrainNumber(it)
                            },
                            textStyle = Typography.bodyLarge
                                .copy(color = MaterialTheme.colorScheme.primary),
                            label = {
                                Text(text = "Номер", color = MaterialTheme.colorScheme.secondary)
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

                        var locomotiveDropDown by remember { mutableStateOf(false) }

                        var fieldLocoValue by remember { mutableStateOf<String?>(null) }

                        if (locomotiveTrain.value != null) {
                            locomotiveTrain.value!!.series?.let {
                                fieldLocoValue = it
                            }
                            locomotiveTrain.value!!.number?.let {
                                fieldLocoValue += " $it"
                            }
                        } else {
                            fieldLocoValue = null
                        }

                        OutlinedTextField(
                            modifier = Modifier
                                .padding(end = 8.dp, top = 8.dp)
                                .weight(0.5f),
                            value = fieldLocoValue ?: "",
                            onValueChange = {},
                            textStyle = Typography.bodyLarge
                                .copy(color = MaterialTheme.colorScheme.primary),
                            placeholder = {
                                Text(
                                    text = "Локомотив",
                                    color = MaterialTheme.colorScheme.secondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = { locomotiveDropDown = true }) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "выбрать локомотив"
                                    )
                                    DropdownMenu(
                                        expanded = locomotiveDropDown,
                                        onDismissRequest = { locomotiveDropDown = false }
                                    ) {
                                        addingRouteViewModel.stateLocoList.forEach { loco ->
                                            DropdownMenuItem(
                                                modifier = Modifier.padding(horizontal = 16.dp),
                                                onClick = {
                                                    locomotiveDropDown = false
                                                    addingTrainViewModel.setLocomotiveTrain(loco)
                                                },
                                                text = {
                                                    Text(
                                                        text = "${loco.series ?: ""} ${loco.number ?: ""}",
                                                        style = Typography.bodyLarge.copy(color = MaterialTheme.colorScheme.primary)
                                                    )
                                                }
                                            )
                                            Divider()
                                        }
                                        DropdownMenuItem(
                                            modifier = Modifier.padding(horizontal = 16.dp),
                                            onClick = {
                                                navController.navigate(Screen.AddingLoco.route)
                                            },
                                            text = {
                                                Text(
                                                    text = "Добавить локомотив",
                                                    style = Typography.bodyLarge.copy(color = MaterialTheme.colorScheme.tertiary)
                                                )
                                            }
                                        )
                                    }
                                }
                            }
                        )
                    }
                }
                item {
                    val focusManager = LocalFocusManager.current
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp)
                    ) {
                        OutlinedTextField(
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .weight(1f),
                            value = weightTrain,
                            onValueChange = {
                                addingTrainViewModel.setTrainWeight(it)
                            },
                            textStyle = Typography.bodyLarge
                                .copy(color = MaterialTheme.colorScheme.primary),
                            label = {
                                Text(text = "Вес", color = MaterialTheme.colorScheme.secondary)
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = {
                                    scope.launch {
                                        focusManager.moveFocus(FocusDirection.Right)
                                    }
                                }
                            ),
                            singleLine = true
                        )

                        OutlinedTextField(
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .weight(1f),
                            value = axleTrain,
                            onValueChange = {
                                addingTrainViewModel.setTrainAxle(it)
                            },
                            textStyle = Typography.bodyLarge
                                .copy(color = MaterialTheme.colorScheme.primary),
                            label = {
                                Text(text = "Оси", color = MaterialTheme.colorScheme.secondary)
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = {
                                    scope.launch {
                                        focusManager.moveFocus(FocusDirection.Right)
                                    }
                                }
                            ),
                            singleLine = true
                        )

                        OutlinedTextField(
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .weight(1f),
                            value = conditionalLengthTrain,
                            onValueChange = {
                                addingTrainViewModel.setTrainConditionalLength(it)
                            },
                            textStyle = Typography.bodyLarge
                                .copy(color = MaterialTheme.colorScheme.primary),
                            label = {
                                Text(text = "у.д.", color = MaterialTheme.colorScheme.secondary)
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
                itemsIndexed(
                    items = stationListTrain,
                    key = { _, item -> item.id }
                ) { index, station ->
                    if (index == 0) {
                        SecondarySpacer()
                    }

                    Box(
                        modifier = Modifier.wrapContentSize(),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        ActionsRow {
                            addingTrainViewModel.removeStation(station)
                        }
                        StationDraggableItem(
                            viewModel = addingTrainViewModel,
                            formState = station,
                            index = index,
                        )
                    }

                }
                item {
                    ClickableText(
                        modifier = Modifier.padding(top = 24.dp),
                        text = AnnotatedString("Добавить станцию"),
                        style = Typography.titleMedium.copy(color = MaterialTheme.colorScheme.tertiary)
                    ) {
                        addingTrainViewModel.addStation(Station())
                        scope.launch {
                            val countItems = scrollState.layoutInfo.totalItemsCount
                            scrollState.animateScrollToItem(countItems)
                        }
                    }
                }
                item { Spacer(modifier = Modifier.padding(70.dp)) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StationDraggableItem(
    viewModel: AddTrainViewModel,
    formState: StationFormState,
    index: Int
) {
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val isFirst = index == 0

    DraggableItem(
        isRevealed = viewModel.revealedItemStationIdsList.contains(
            formState.id
        ),
        onExpand = {
            viewModel.onExpandedStationItem(formState.id)
        },
        onCollapse = {
            viewModel.onCollapsedStationItem(formState.id)
        },
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            val (stationField, timeArrivalField, timeDepartureField) = createRefs()

            OutlinedTextField(
                modifier = Modifier
                    .constrainAs(stationField) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        end.linkTo(timeArrivalField.start)
                        width = Dimension.percent(0.6f)
                    }
                    .padding(end = 8.dp, bottom = 8.dp),
                value = formState.station.data ?: "",
                onValueChange = {
                    viewModel.createEventStation(
                        StationEvent.EnteredStationName(
                            index = index,
                            data = it
                        )
                    )
                },
                textStyle = Typography.bodyLarge
                    .copy(color = MaterialTheme.colorScheme.primary),
                label = {
                    Text(
                        text = "Станция",
                        color = MaterialTheme.colorScheme.secondary
                    )
                },
                keyboardOptions = KeyboardOptions(
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
            val stationState = viewModel.stationListState[index]
            val arrival = stationState.arrival.data

            val arrivalToLocalDateTime: LocalDateTime? =
                arrival?.let {
                    LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(it),
                        ZoneId.systemDefault()
                    )
                }

            val calendarArrivalState = rememberUseCaseState()
            val clockArrivalState = rememberUseCaseState()

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
                    localDateArrival = LocalDate.of(date.year, date.month, date.dayOfMonth)
                    clockArrivalState.show()
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
                state = clockArrivalState,
                selection = ClockSelection.HoursMinutes{ hours, minutes ->
                    localTimeArrival = LocalTime.of(hours, minutes)
                    dateAndTimeArrival = LocalDateTime.of(localDateArrival, localTimeArrival)
                    val dateInLong = dateAndTimeArrival!!.toLong()
                    viewModel.createEventStation(
                        StationEvent.EnteredArrivalTime(index = index, data = dateInLong)
                    )
                    viewModel.createEventStation(
                        StationEvent.FocusChange(index = index, fieldName = StationDataType.ARRIVAL)
                    )
                },
                header = Header.Default(
                    title = "Отправление"
                ),
                config = ClockConfig(
                    is24HourFormat = true,
                )
            )

            val departure = stationState.departure.data
            val departureToLocalDateTime: LocalDateTime? =
                departure?.let {
                    LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(it),
                        ZoneId.systemDefault()
                    )
                }
            val calendarDepartureState = rememberUseCaseState()
            val clockDepartureState = rememberUseCaseState()

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
                    localDateDeparture = LocalDate.of(date.year, date.month, date.dayOfMonth)
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
                selection = ClockSelection.HoursMinutes{ hours, minutes ->
                    localTimeDeparture = LocalTime.of(hours, minutes)
                    dateAndTimeDeparture = LocalDateTime.of(localDateDeparture, localTimeDeparture)
                    val dateInLong = dateAndTimeDeparture!!.toLong()
                    viewModel.createEventStation(
                        StationEvent.EnteredDepartureTime(index = index, data = dateInLong)
                    )
                    viewModel.createEventStation(
                        StationEvent.FocusChange(index = index, fieldName = StationDataType.DEPARTURE)
                    )
                },
                header = Header.Default(
                    title = "Отправление"
                ),
                config = ClockConfig(
                    is24HourFormat = true,
                )
            )

            Box(
                modifier = Modifier
                    .constrainAs(timeArrivalField) {
                        start.linkTo(stationField.end)
                        top.linkTo(stationField.top)
                        end.linkTo(timeDepartureField.start)
                        bottom.linkTo(stationField.bottom)
                        width = Dimension.percent(0.2f)
                        height = Dimension.fillToConstraints
                    }
                    .padding(end = 8.dp, top = 8.dp, bottom = 8.dp)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = ShapeBackground.extraSmall
                    )
                    .background(
                        color = if(!stationState.formValid.data && stationState.formValid.type == StationDataType.ARRIVAL) {
                            MaterialTheme.colorScheme.errorContainer
                        } else {
                            Color.Transparent
                        }
                    )
                    .clickable(!isFirst) {
                        calendarArrivalState.show()
                    },
                contentAlignment = Alignment.Center
            ) {
                if (!isFirst) {
                    val textTimeArrival = formState.arrival.data?.let { millis ->
                        SimpleDateFormat(
                            DateAndTimeFormat.TIME_FORMAT,
                            Locale.getDefault()
                        ).format(
                            millis
                        )
                    } ?: DateAndTimeFormat.DEFAULT_TIME_TEXT

                    Text(
                        text = textTimeArrival,
                        color = setTextColor(any = formState.arrival.data)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .constrainAs(timeDepartureField) {
                        start.linkTo(timeArrivalField.end)
                        top.linkTo(timeArrivalField.top)
                        end.linkTo(parent.end)
                        bottom.linkTo(timeArrivalField.bottom)
                        width = Dimension.percent(0.2f)
                        height = Dimension.fillToConstraints
                    }
                    .padding(end = 8.dp, top = 8.dp, bottom = 8.dp)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = ShapeBackground.extraSmall
                    )
                    .background(
                        color = if(!stationState.formValid.data && stationState.formValid.type == StationDataType.DEPARTURE) {
                            MaterialTheme.colorScheme.errorContainer
                        } else {
                            Color.Transparent
                        }
                    )
                    .clickable {
                        calendarDepartureState.show()
                    },
                contentAlignment = Alignment.Center
            ) {
                val textTimeDeparture = formState.departure.data?.let { millis ->
                    SimpleDateFormat(
                        DateAndTimeFormat.TIME_FORMAT,
                        Locale.getDefault()
                    ).format(
                        millis
                    )
                } ?: DateAndTimeFormat.DEFAULT_TIME_TEXT

                Text(
                    text = textTimeDeparture,
                    color = setTextColor(any = formState.departure.data)
                )

            }
        }
    }
}
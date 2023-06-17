package com.example.traindriver.ui.screen.adding_screen.adding_loco

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.Lifecycle
import com.example.traindriver.R
import com.example.traindriver.domain.entity.SectionDiesel
import com.example.traindriver.domain.entity.SectionElectric
import com.example.traindriver.ui.screen.adding_screen.*
import com.example.traindriver.ui.screen.adding_screen.custom_tab.CustomTab
import com.example.traindriver.ui.screen.adding_screen.state_holder.*
import com.example.traindriver.ui.screen.signin_screen.elements.SecondarySpacer
import com.example.traindriver.ui.screen.viewing_route_screen.element.BottomShadow
import com.example.traindriver.ui.screen.viewing_route_screen.element.isScrollInInitialState
import com.example.traindriver.ui.screen.viewing_route_screen.element.setTextColor
import com.example.traindriver.ui.theme.ShapeBackground
import com.example.traindriver.ui.theme.TrainDriverTheme
import com.example.traindriver.ui.theme.Typography
import com.example.traindriver.ui.util.DarkLightPreviews
import com.example.traindriver.ui.util.DateAndTimeFormat
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.traindriver.domain.entity.Calculation
import com.example.traindriver.ui.util.OnLifecycleEvent
import com.example.traindriver.ui.util.double_util.plus
import com.example.traindriver.ui.util.double_util.str
import com.maxkeppeker.sheets.core.models.base.Header
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.maxkeppeler.sheets.clock.ClockDialog
import com.maxkeppeler.sheets.clock.models.ClockConfig
import com.maxkeppeler.sheets.clock.models.ClockSelection
import java.time.*

@OptIn(
    ExperimentalPagerApi::class,
    ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class
)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun AddingLocoScreen(
    navController: NavController,
    locoId: String? = null,
    addingRouteViewModel: AddingViewModel,
) {
    val addingLocoViewModel: AddingLocoViewModel = viewModel()
    OnLifecycleEvent { _, event ->
        when (event) {
            Lifecycle.Event.ON_CREATE -> {
                addingLocoViewModel.setData(
                    locomotive = addingRouteViewModel.stateLocoList.find {
                        it.id == locoId
                    },
                    timeState = addingRouteViewModel.timeEditState
                )
            }
            else -> {}
        }
    }

    var openBottomSheet by rememberSaveable { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState()

    val coefficientState: MutableState<Pair<Int, String>> = remember {
        mutableStateOf(Pair(0, "0.0"))
    }
    val refuelState: MutableState<Pair<Int, String>> = remember {
        mutableStateOf(Pair(0, "0.0"))
    }
    val scope = rememberCoroutineScope()

    var currentSheet: BottomSheetLoco? by remember {
        mutableStateOf(null)
    }

    val openSheet: (BottomSheetLoco) -> Unit = { screen ->
        scope.launch {
            currentSheet = screen
            openBottomSheet = !openBottomSheet
        }
    }

    val closeSheet: () -> Unit = {
        scope.launch {
            bottomSheetState.hide()
            openBottomSheet = false
        }
    }

    if (openBottomSheet) {
        ModalBottomSheet(
            sheetState = bottomSheetState,
            onDismissRequest = closeSheet,
            containerColor = MaterialTheme.colorScheme.background
        ) {
            currentSheet?.let { sheet ->
                SheetLayoutLoco(
                    sheet = sheet,
                    viewModel = addingLocoViewModel,
                    closeSheet = closeSheet,
                    bottomSheetState = bottomSheetState,
                    coefficientState = coefficientState,
                    refuelState = refuelState
                )
            }
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxWidth(),
        topBar = {
            MediumTopAppBar(
                title = {
                    Text(
                        text = "Локомотив",
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
                            addingLocoViewModel.addLocomotiveInRoute(
                                addingRouteViewModel.stateLocoList
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
                                    addingLocoViewModel.clearField()
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
        val scrollState = rememberLazyListState()
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues),
        ) {
            val (topShadow, lazyColumn) = createRefs()
            val number = addingLocoViewModel.numberLocoState
            val series = addingLocoViewModel.seriesLocoState
            val pagerState = rememberPagerState(pageCount = 2, initialPage = 0)

            LaunchedEffect(addingLocoViewModel.pagerState) {
                scope.launch {
                    pagerState.scrollToPage(addingLocoViewModel.pagerState)
                }
            }

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
                                .weight(1f),
                            value = series,
                            textStyle = Typography.bodyLarge
                                .copy(color = MaterialTheme.colorScheme.primary),
                            placeholder = {
                                Text(text = "Серия", color = MaterialTheme.colorScheme.secondary)
                            },
                            onValueChange = {
                                addingLocoViewModel.setSeriesLoco(it)
                            },
                            keyboardOptions = KeyboardOptions(
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
                                .padding(start = 8.dp)
                                .weight(1f),
                            value = number,
                            textStyle = Typography.bodyLarge
                                .copy(color = MaterialTheme.colorScheme.primary),
                            placeholder = {
                                Text(
                                    text = "Номер",
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            },
                            onValueChange = {
                                addingLocoViewModel.setNumberLoco(it)
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
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
                    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

                    CustomTab(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        items = listOf("Тепловоз", "Электровоз"),
                        tabWidth = (screenWidth - 48.dp) / 2,
                        selectedItemIndex = pagerState.currentPage,
                        pagerState = pagerState,
                    )
                }
                item {
                    val stateAccepted = addingLocoViewModel.acceptedTimeState.value
                    val startAcceptedTime = stateAccepted.startAccepted.time
                    val endAcceptedTime = stateAccepted.endAccepted.time

                    val calendarStartAcceptedState = rememberUseCaseState()
                    val timeStartAcceptedState = rememberUseCaseState()

                    val dateStartAcceptedLocalDateTime: LocalDateTime? =
                        startAcceptedTime?.let {
                            LocalDateTime.ofInstant(
                                Instant.ofEpochMilli(it),
                                ZoneId.systemDefault()
                            )
                        }

                    var localDateAcceptedStart by remember {
                        mutableStateOf<LocalDate?>(null)
                    }
                    var localTimeAcceptedStart by remember {
                        mutableStateOf<LocalTime?>(null)
                    }
                    var dateAndTimeAcceptedStartWork by remember {
                        mutableStateOf<LocalDateTime?>(null)
                    }

                    CalendarDialog(
                        state = calendarStartAcceptedState,
                        selection = CalendarSelection.Date(
                            selectedDate = dateStartAcceptedLocalDateTime?.toLocalDate()
                        ) { date ->
                            localDateAcceptedStart =
                                LocalDate.of(date.year, date.month, date.dayOfMonth)
                            timeStartAcceptedState.show()
                        },
                        header = Header.Default(
                            title = "Начало приемки"
                        ),
                        config = CalendarConfig(
                            monthSelection = true,
                            yearSelection = true,
                        ),
                    )

                    ClockDialog(
                        state = timeStartAcceptedState,
                        selection = ClockSelection.HoursMinutes { hours, minutes ->
                            localTimeAcceptedStart = LocalTime.of(hours, minutes)
                            dateAndTimeAcceptedStartWork =
                                LocalDateTime.of(localDateAcceptedStart, localTimeAcceptedStart)
                            val dateInLong = dateAndTimeAcceptedStartWork!!.toLong()
                            addingLocoViewModel.createEventAccepted(
                                AcceptedEvent.EnteredStartAccepted(dateInLong)
                            )
                            addingLocoViewModel.createEventAccepted(
                                AcceptedEvent.FocusChange(
                                    AcceptedType.START
                                )
                            )
                        },
                        header = Header.Default(
                            title = "Начало работы"
                        ),
                        config = ClockConfig(
                            is24HourFormat = true,
                        )
                    )

                    val calendarEndAcceptedState = rememberUseCaseState()
                    val timeEndAcceptedState = rememberUseCaseState()

                    val dateEndAcceptedLocalDateTime: LocalDateTime? =
                        endAcceptedTime?.let {
                            LocalDateTime.ofInstant(
                                Instant.ofEpochMilli(it),
                                ZoneId.systemDefault()
                            )
                        }

                    var localDateAcceptedEnd by remember {
                        mutableStateOf<LocalDate?>(null)
                    }
                    var localTimeAcceptedEnd by remember {
                        mutableStateOf<LocalTime?>(null)
                    }
                    var dateAndTimeAcceptedEndWork by remember {
                        mutableStateOf<LocalDateTime?>(null)
                    }

                    CalendarDialog(
                        state = calendarEndAcceptedState,
                        selection = CalendarSelection.Date(
                            selectedDate = dateEndAcceptedLocalDateTime?.toLocalDate()
                        ) { date ->
                            localDateAcceptedEnd =
                                LocalDate.of(date.year, date.month, date.dayOfMonth)
                            timeEndAcceptedState.show()
                        },
                        header = Header.Default(
                            title = "Окончание приемки"
                        ),
                        config = CalendarConfig(
                            monthSelection = true,
                            yearSelection = true,
                        ),
                    )

                    ClockDialog(
                        state = timeEndAcceptedState,
                        selection = ClockSelection.HoursMinutes { hours, minutes ->
                            localTimeAcceptedEnd = LocalTime.of(hours, minutes)
                            dateAndTimeAcceptedEndWork =
                                LocalDateTime.of(localDateAcceptedEnd, localTimeAcceptedEnd)
                            val dateInLong = dateAndTimeAcceptedEndWork!!.toLong()
                            addingLocoViewModel.createEventAccepted(
                                AcceptedEvent.EnteredEndAccepted(dateInLong)
                            )
                            addingLocoViewModel.createEventAccepted(
                                AcceptedEvent.FocusChange(
                                    AcceptedType.END
                                )
                            )
                        },
                        header = Header.Default(
                            title = "Окончание приемки"
                        ),
                        config = ClockConfig(
                            is24HourFormat = true,
                        )
                    )

                    Column(
                        modifier = Modifier
                            .padding(top = 32.dp)
                            .border(
                                width = 1.dp,
                                shape = ShapeBackground.small,
                                color = MaterialTheme.colorScheme.outline
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (!stateAccepted.formValid) {
                            Row(
                                modifier = Modifier
                                    .padding(top = 12.dp, start = 16.dp, end = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_error_24),
                                    tint = Color.Red,
                                    contentDescription = null
                                )
                                Text(
                                    modifier = Modifier.padding(start = 8.dp),
                                    text = stateAccepted.errorMessage,
                                    style = Typography.bodySmall.copy(color = Color.Red),
                                    color = Color.Red
                                )
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                modifier = Modifier.padding(start = 16.dp),
                                text = "Приемка",
                                style = Typography.bodyLarge.copy(color = MaterialTheme.colorScheme.secondary)
                            )

                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clickable {
                                            calendarStartAcceptedState.show()
                                        }
                                        .padding(horizontal = 18.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    val timeStartText = startAcceptedTime?.let { millis ->
                                        SimpleDateFormat(
                                            DateAndTimeFormat.TIME_FORMAT,
                                            Locale.getDefault()
                                        ).format(
                                            millis
                                        )
                                    } ?: DateAndTimeFormat.DEFAULT_TIME_TEXT

                                    Text(
                                        text = timeStartText,
                                        style = Typography.bodyLarge,
                                        color = setTextColor(startAcceptedTime)
                                    )
                                }
                                Text(" - ")
                                Box(
                                    modifier = Modifier
                                        .padding(18.dp)
                                        .clickable {
                                            calendarEndAcceptedState.show()
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    val timeStartText = endAcceptedTime?.let { millis ->
                                        SimpleDateFormat(
                                            DateAndTimeFormat.TIME_FORMAT,
                                            Locale.getDefault()
                                        ).format(
                                            millis
                                        )
                                    } ?: DateAndTimeFormat.DEFAULT_TIME_TEXT

                                    Text(
                                        text = timeStartText,
                                        style = Typography.bodyLarge,
                                        color = setTextColor(endAcceptedTime)
                                    )
                                }
                            }
                        }
                    }
                }
                item {
                    val stateDelivery = addingLocoViewModel.deliveryTimeState.value
                    val startDeliveryTime = stateDelivery.startDelivered.time
                    val endDeliveryTime = stateDelivery.endDelivered.time

                    val calendarStartDeliveryState = rememberUseCaseState()
                    val timeStartDeliveryState = rememberUseCaseState()

                    val dateStartDeliveryLocalDateTime: LocalDateTime? =
                        startDeliveryTime?.let {
                            LocalDateTime.ofInstant(
                                Instant.ofEpochMilli(it),
                                ZoneId.systemDefault()
                            )
                        }

                    var localDateDeliveryStart by remember {
                        mutableStateOf<LocalDate?>(null)
                    }
                    var localTimeDeliveryStart by remember {
                        mutableStateOf<LocalTime?>(null)
                    }
                    var dateAndTimeDeliveryStartWork by remember {
                        mutableStateOf<LocalDateTime?>(null)
                    }

                    CalendarDialog(
                        state = calendarStartDeliveryState,
                        selection = CalendarSelection.Date(
                            selectedDate = dateStartDeliveryLocalDateTime?.toLocalDate()
                        ) { date ->
                            localDateDeliveryStart =
                                LocalDate.of(date.year, date.month, date.dayOfMonth)
                            timeStartDeliveryState.show()
                        },
                        header = Header.Default(
                            title = "Начало сдачи"
                        ),
                        config = CalendarConfig(
                            monthSelection = true,
                            yearSelection = true,
                        ),
                    )

                    ClockDialog(
                        state = timeStartDeliveryState,
                        selection = ClockSelection.HoursMinutes { hours, minutes ->
                            localTimeDeliveryStart = LocalTime.of(hours, minutes)
                            dateAndTimeDeliveryStartWork =
                                LocalDateTime.of(localDateDeliveryStart, localTimeDeliveryStart)
                            val dateInLong = dateAndTimeDeliveryStartWork!!.toLong()
                            addingLocoViewModel.createEventDelivery(
                                DeliveryEvent.EnteredStartDelivery(dateInLong)
                            )
                            addingLocoViewModel.createEventDelivery(
                                DeliveryEvent.FocusChange(
                                    DeliveredType.START
                                )
                            )
                        },
                        header = Header.Default(
                            title = "Начало сдачи"
                        ),
                        config = ClockConfig(
                            is24HourFormat = true,
                        )
                    )

                    val calendarEndDeliveryState = rememberUseCaseState()
                    val timeEndDeliveryState = rememberUseCaseState()

                    val dateEndDeliveryLocalDateTime: LocalDateTime? =
                        startDeliveryTime?.let {
                            LocalDateTime.ofInstant(
                                Instant.ofEpochMilli(it),
                                ZoneId.systemDefault()
                            )
                        }

                    var localDateDeliveryEnd by remember {
                        mutableStateOf<LocalDate?>(null)
                    }
                    var localTimeDeliveryEnd by remember {
                        mutableStateOf<LocalTime?>(null)
                    }
                    var dateAndTimeDeliveryEndWork by remember {
                        mutableStateOf<LocalDateTime?>(null)
                    }

                    CalendarDialog(
                        state = calendarEndDeliveryState,
                        selection = CalendarSelection.Date(
                            selectedDate = dateEndDeliveryLocalDateTime?.toLocalDate()
                        ) { date ->
                            localDateDeliveryEnd =
                                LocalDate.of(date.year, date.month, date.dayOfMonth)
                            timeEndDeliveryState.show()
                        },
                        header = Header.Default(
                            title = "Окончание сдачи"
                        ),
                        config = CalendarConfig(
                            monthSelection = true,
                            yearSelection = true,
                        ),
                    )

                    ClockDialog(
                        state = timeEndDeliveryState,
                        selection = ClockSelection.HoursMinutes { hours, minutes ->
                            localTimeDeliveryEnd = LocalTime.of(hours, minutes)
                            dateAndTimeDeliveryEndWork =
                                LocalDateTime.of(localDateDeliveryEnd, localTimeDeliveryEnd)
                            val dateInLong = dateAndTimeDeliveryEndWork!!.toLong()
                            addingLocoViewModel.createEventDelivery(
                                DeliveryEvent.EnteredEndDelivery(dateInLong)
                            )
                            addingLocoViewModel.createEventDelivery(
                                DeliveryEvent.FocusChange(
                                    DeliveredType.END
                                )
                            )
                        },
                        header = Header.Default(
                            title = "Окончание сдачи"
                        ),
                        config = ClockConfig(
                            is24HourFormat = true,
                        )
                    )

                    Column(
                        modifier = Modifier
                            .padding(top = 12.dp)
                            .border(
                                width = 1.dp,
                                shape = ShapeBackground.small,
                                color = MaterialTheme.colorScheme.outline
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (!stateDelivery.formValid) {
                            Row(
                                modifier = Modifier
                                    .padding(top = 12.dp, start = 16.dp, end = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_error_24),
                                    tint = Color.Red,
                                    contentDescription = null
                                )
                                Text(
                                    modifier = Modifier.padding(start = 8.dp),
                                    text = stateDelivery.errorMessage,
                                    style = Typography.bodySmall.copy(color = Color.Red),
                                    color = Color.Red
                                )
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                modifier = Modifier.padding(start = 16.dp),
                                text = "Сдача",
                                style = Typography.bodyLarge.copy(color = MaterialTheme.colorScheme.secondary)
                            )

                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clickable {
                                            calendarStartDeliveryState.show()
                                        }
                                        .padding(horizontal = 18.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    val timeStartText = startDeliveryTime?.let { millis ->
                                        SimpleDateFormat(
                                            DateAndTimeFormat.TIME_FORMAT,
                                            Locale.getDefault()
                                        ).format(
                                            millis
                                        )
                                    } ?: DateAndTimeFormat.DEFAULT_TIME_TEXT

                                    Text(
                                        text = timeStartText,
                                        style = Typography.bodyLarge,
                                        color = setTextColor(startDeliveryTime)
                                    )
                                }
                                Text(" - ")
                                Box(
                                    modifier = Modifier
                                        .padding(18.dp)
                                        .clickable {
                                            calendarEndDeliveryState.show()
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    val timeEndText = endDeliveryTime?.let { millis ->
                                        SimpleDateFormat(
                                            DateAndTimeFormat.TIME_FORMAT,
                                            Locale.getDefault()
                                        ).format(
                                            millis
                                        )
                                    } ?: DateAndTimeFormat.DEFAULT_TIME_TEXT

                                    Text(
                                        text = timeEndText,
                                        style = Typography.bodyLarge,
                                        color = setTextColor(endDeliveryTime)
                                    )
                                }
                            }
                        }
                    }
                }
                when (pagerState.currentPage) {
                    0 -> {
                        val list = addingLocoViewModel.dieselSectionListState
                        val revealedSectionIds =
                            addingLocoViewModel.revealedItemDieselSectionIdsList
                        itemsIndexed(
                            items = list,
                            key = { _, item -> item.sectionId }
                        ) { index, item ->
                            if (index == 0) {
                                SecondarySpacer()
                            } else {
                                Spacer(modifier = Modifier.padding(dimensionResource(id = R.dimen.secondary_padding_between_view) / 2))
                            }
                            Box(
                                modifier = Modifier
                                    .animateItemPlacement(
                                        animationSpec = tween(
                                            durationMillis = 500,
                                            delayMillis = 100,
                                            easing = FastOutLinearInEasing
                                        )
                                    )
                                    .wrapContentSize(),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                ActionsRow(
                                    onDelete = { addingLocoViewModel.removeDieselSection(item) }
                                )
                                DraggableDieselItem(
                                    item = item,
                                    index = index,
                                    viewModel = addingLocoViewModel,
                                    coefficientState = coefficientState,
                                    refuelState = refuelState,
                                    openSheet = openSheet,
                                    isRevealed = revealedSectionIds.contains(item.sectionId),
                                    onCollapse = {
                                        addingLocoViewModel.onCollapsedDieselSection(
                                            item.sectionId
                                        )
                                    },
                                    onExpand = {
                                        addingLocoViewModel.onExpandedDieselSection(
                                            item.sectionId
                                        )
                                    },
                                )
                            }
                            if (index == list.lastIndex && index > 0) {
                                var overResult: Double? = null
                                addingLocoViewModel.dieselSectionListState.forEach {
                                    val accepted = it.accepted.data?.toDoubleOrNull()
                                    val delivery = it.delivery.data?.toDoubleOrNull()
                                    val refuel = it.refuel.data?.toDoubleOrNull()
                                    val result = Calculation.getTotalFuelConsumption(
                                        accepted, delivery, refuel
                                    )
                                    overResult += result
                                }
                                overResult?.let {
                                    Text(
                                        text = "Всего расход = ${maskInLiter(it.str())}",
                                        style = Typography.bodyMedium.copy(color = MaterialTheme.colorScheme.secondary),
                                    )
                                }
                            }
                        }
                    }
                    1 -> {
                        val list = addingLocoViewModel.electricSectionListState
                        val revealedSectionIds =
                            addingLocoViewModel.revealedItemElectricSectionIdsList
                        itemsIndexed(
                            items = list,
                            key = { _, item -> item.sectionId }
                        ) { index, item ->
                            if (index == 0) {
                                SecondarySpacer()
                            } else {
                                Spacer(modifier = Modifier.padding(dimensionResource(id = R.dimen.secondary_padding_between_view) / 2))
                            }
                            Box(
                                modifier = Modifier
                                    .animateItemPlacement(
                                        animationSpec = tween(
                                            durationMillis = 500,
                                            delayMillis = 100,
                                            easing = FastOutLinearInEasing
                                        )
                                    )
                                    .wrapContentSize()
                                    .padding(bottom = 12.dp),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                ActionsRow(
                                    onDelete = { addingLocoViewModel.removeElectricSection(item) }
                                )
                                DraggableElectricItem(
                                    item = item,
                                    isRevealed = revealedSectionIds.contains(item.sectionId),
                                    onExpand = {
                                        addingLocoViewModel.onExpandedElectricSection(
                                            item.sectionId
                                        )
                                    },
                                    onCollapse = {
                                        addingLocoViewModel.onCollapsedElectricSection(
                                            item.sectionId
                                        )
                                    },
                                    index = index,
                                    viewModel = addingLocoViewModel
                                )
                            }
                            if (index == list.lastIndex && index > 0) {
                                var overResult: Double? = null
                                var overRecovery: Double? = null

                                addingLocoViewModel.electricSectionListState.forEach {
                                    val accepted = it.accepted.data?.toDoubleOrNull()
                                    val delivery = it.delivery.data?.toDoubleOrNull()
                                    val acceptedRecovery =
                                        it.recoveryAccepted.data?.toDoubleOrNull()
                                    val deliveryRecovery =
                                        it.recoveryDelivery.data?.toDoubleOrNull()

                                    val result = Calculation.getTotalEnergyConsumption(
                                        accepted, delivery
                                    )
                                    val resultRecovery = Calculation.getTotalEnergyConsumption(
                                        acceptedRecovery, deliveryRecovery
                                    )
                                    overResult += result
                                    overRecovery += resultRecovery
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    overResult?.let {
                                        Text(
                                            text = "Всего расход = ${it.str()}",
                                            style = Typography.bodyMedium.copy(color = MaterialTheme.colorScheme.secondary),
                                        )
                                    }
                                    overRecovery?.let {
                                        Text(
                                            text = "Всего рекуперация = ${it.str()}",
                                            style = Typography.bodyMedium.copy(color = MaterialTheme.colorScheme.secondary),
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                item {
                    ClickableText(
                        modifier = Modifier.padding(top = 24.dp),
                        text = AnnotatedString("Добавить секцию"),
                        style = Typography.titleMedium.copy(color = MaterialTheme.colorScheme.tertiary)
                    ) {
                        when (pagerState.currentPage) {
                            0 -> addingLocoViewModel.addDieselSection(SectionDiesel())
                            1 -> addingLocoViewModel.addElectricSection(SectionElectric())
                        }

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
fun SheetLayoutLoco(
    sheet: BottomSheetLoco,
    viewModel: AddingLocoViewModel,
    closeSheet: () -> Unit,
    bottomSheetState: SheetState,
    coefficientState: MutableState<Pair<Int, String>>,
    refuelState: MutableState<Pair<Int, String>>
) {
    when (sheet) {
        is BottomSheetLoco.CoefficientSheet -> {
            BottomSheetCoefficient(
                viewModel = viewModel,
                coefficientData = coefficientState,
                sheetState = bottomSheetState,
                closeSheet = closeSheet
            )
        }
        is BottomSheetLoco.RefuelSheet -> {
            BottomSheetRefuel(
                viewModel = viewModel,
                refuelData = refuelState,
                sheetState = bottomSheetState,
                closeSheet = closeSheet
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetRefuel(
    viewModel: AddingLocoViewModel,
    refuelData: MutableState<Pair<Int, String>>,
    sheetState: SheetState,
    closeSheet: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val requester = FocusRequester()
    val focusManager = LocalFocusManager.current
    val text = refuelData.value.second
    val textData = TextFieldValue(
        text = text,
        selection = TextRange(text.length)
    )

    Column(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Экипировка", style = Typography.titleLarge,
            textAlign = TextAlign.Center
        )
        OutlinedTextField(
            modifier = Modifier
                .focusable(true)
                .focusRequester(requester)
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 65.dp),
            value = textData,
            onValueChange = {
                viewModel.createEventDieselSection(
                    DieselSectionEvent.EnteredRefuel(
                        index = refuelData.value.first,
                        data = it.text.toDoubleOrNull()
                    )
                )
                viewModel.createEventDieselSection(
                    DieselSectionEvent.FocusChange(
                        index = refuelData.value.first,
                        fieldName = DieselSectionType.REFUEL
                    )
                )
                refuelData.value = refuelData.value.copy(
                    second = it.text
                )
            },
            textStyle = Typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.primary
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    scope.launch {
                        focusManager.clearFocus()
                        closeSheet.invoke()
                    }
                }
            )
        )
    }

    if (sheetState.isVisible) {
        SideEffect {
            requester.requestFocus()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BottomSheetCoefficient(
    viewModel: AddingLocoViewModel,
    coefficientData: MutableState<Pair<Int, String>>,
    sheetState: SheetState,
    closeSheet: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val requester = FocusRequester()
    val focusManager = LocalFocusManager.current
    val text = coefficientData.value.second
    val textData = TextFieldValue(
        text = text,
        selection = TextRange(text.length)
    )
    Column(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Коэффициент", style = Typography.titleLarge,
            textAlign = TextAlign.Center
        )
        OutlinedTextField(
            modifier = Modifier
                .focusable(true)
                .focusRequester(requester)
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 65.dp),
            value = textData,
            onValueChange = {
                viewModel.createEventDieselSection(
                    DieselSectionEvent.EnteredCoefficient(
                        index = coefficientData.value.first,
                        data = it.text.toDoubleOrNull()
                    )
                )
                viewModel.createEventDieselSection(
                    DieselSectionEvent.FocusChange(
                        index = coefficientData.value.first,
                        fieldName = DieselSectionType.COEFFICIENT
                    )
                )
                coefficientData.value = coefficientData.value.copy(
                    second = it.text
                )
            },
            textStyle = Typography.bodyLarge.copy(color = MaterialTheme.colorScheme.primary),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    scope.launch {
                        focusManager.clearFocus()
                        closeSheet.invoke()
                    }
                }
            )
        )
    }
    if (sheetState.isVisible) {
        SideEffect {
            requester.requestFocus()
        }
    }

}

@Composable
@DarkLightPreviews
private fun AddingLocoPreviews() {
    TrainDriverTheme {
//        AddingLocoScreen(viewModel = viewModel(), timeState = mutableStateOf<WorkTimeEditState>(WorkTimeEditState()))
    }
}

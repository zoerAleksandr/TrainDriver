package com.example.traindriver.ui.screen.adding_screen.bottom_sheet_screen.adding_loco

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
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
import com.example.traindriver.domain.entity.Locomotive
import com.example.traindriver.domain.entity.SectionDiesel
import com.example.traindriver.domain.entity.SectionElectric
import com.example.traindriver.ui.element_screen.OutlinedTextFieldCustom
import com.example.traindriver.ui.screen.adding_screen.*
import com.example.traindriver.ui.screen.adding_screen.bottom_sheet_screen.BottomSheetWithCloseDialog
import com.example.traindriver.ui.screen.adding_screen.custom_tab.CustomTab
import com.example.traindriver.ui.screen.adding_screen.state_holder.*
import com.example.traindriver.ui.screen.signin_screen.elements.SecondarySpacer
import com.example.traindriver.ui.screen.viewing_route_screen.element.BottomShadow
import com.example.traindriver.ui.screen.viewing_route_screen.element.isScrollInInitialState
import com.example.traindriver.ui.screen.viewing_route_screen.element.setTextColor
import com.example.traindriver.ui.theme.ShapeBackground
import com.example.traindriver.ui.theme.ShapeSurface
import com.example.traindriver.ui.theme.TrainDriverTheme
import com.example.traindriver.ui.theme.Typography
import com.example.traindriver.ui.util.ClickableTextTrainDriver
import com.example.traindriver.ui.util.DarkLightPreviews
import com.example.traindriver.ui.util.DateAndTimeFormat
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.traindriver.ui.util.OnLifecycleEvent

@OptIn(
    ExperimentalPagerApi::class,
    ExperimentalMaterialApi::class,
    ExperimentalFoundationApi::class
)
@Composable
fun AddingLocoScreen(
    locomotive: Locomotive? = null,
    timeState: State<WorkTimeEditState>,
    viewModel: AddingLocoViewModel = viewModel(),
    stateLocomotiveList: SnapshotStateList<Locomotive>,
    closeAddingLocoScreen: () -> Unit
) {
    OnLifecycleEvent { _, event ->
        when (event) {
            Lifecycle.Event.ON_CREATE -> {
                viewModel.setData(
                    locomotive = locomotive,
                    timeState = timeState
                )
            }
            else -> {}
        }
    }

    val bottomSheetState =
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
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

    if (!bottomSheetState.isVisible) currentSheet = null

    val openSheet: (BottomSheetLoco) -> Unit = { screen ->
        scope.launch {
            currentSheet = screen
            bottomSheetState.show()
        }
    }

    val closeSheet: () -> Unit = {
        scope.launch {
            bottomSheetState.hide()
        }
    }

    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetShape = ShapeSurface.medium,
        sheetBackgroundColor = MaterialTheme.colors.background,
        sheetContent = {
            Spacer(modifier = Modifier.height(1.dp))
            currentSheet?.let { sheet ->
                SheetLayoutLoco(
                    sheet = sheet,
                    viewModel = viewModel,
                    closeSheet = closeSheet,
                    bottomSheetState = bottomSheetState,
                    coefficientState = coefficientState,
                    refuelState = refuelState
                )
            }
        }
    ) {
        val scrollState = rememberLazyListState()

        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 56.dp),
        ) {
            val (saveButton, menu, title, divider, topShadow, lazyColumn) = createRefs()
            val number = viewModel.numberLocoState
            val series = viewModel.seriesLocoState
            val pagerState = rememberPagerState(pageCount = 2, initialPage = 0)

            LaunchedEffect(viewModel.pagerState) {
                scope.launch {
                    pagerState.scrollToPage(viewModel.pagerState)
                }
            }

            var dropDownExpanded by remember { mutableStateOf(false) }

            IconButton(
                modifier = Modifier
                    .constrainAs(menu) {
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                    }
                    .padding(end = 4.dp),
                onClick = {
                    dropDownExpanded = true
                })
            {
                Icon(
                    painter = painterResource(id = R.drawable.more_menu),
                    contentDescription = null,
                    tint = MaterialTheme.colors.primary
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
                        }
                    ) {
                        Text(
                            text = "Очистить",
                            style = Typography.body1.copy(color = MaterialTheme.colors.primary)
                        )
                    }
                }
            }

            Text(
                modifier = Modifier
                    .constrainAs(saveButton) {
                        top.linkTo(menu.top)
                        bottom.linkTo(menu.bottom)
                        end.linkTo(menu.start)
                    }
                    .clickable {
                        closeAddingLocoScreen.invoke()
                        viewModel.addLocomotiveInRoute(stateLocomotiveList)
                    },
                text = "Сохранить",
                style = Typography.button.copy(color = MaterialTheme.colors.secondaryVariant)
            )

            Text(
                modifier = Modifier
                    .constrainAs(title) {
                        top.linkTo(saveButton.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .padding(top = 12.dp),
                text = "Локомотив",
                style = Typography.subtitle1.copy(color = MaterialTheme.colors.primary)
            )

            Divider(modifier = Modifier
                .constrainAs(divider) {
                    top.linkTo(title.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
                .padding(top = 12.dp)
            )

            AnimatedVisibility(
                modifier = Modifier
                    .zIndex(1f)
                    .constrainAs(topShadow) {
                        top.linkTo(divider.bottom)
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
                        top.linkTo(divider.bottom)
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
                            .padding(top = 16.dp)
                    ) {
                        OutlinedTextFieldCustom(
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .weight(1f),
                            value = series,
                            labelText = "Серия",
                            onValueChange = {
                                viewModel.setSeriesLoco(it)
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
                            )
                        )
                        OutlinedTextFieldCustom(
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .weight(1f),
                            value = number,
                            labelText = "Номер",
                            onValueChange = {
                                viewModel.setNumberLoco(it)
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
                            )
                        )
                    }
                }
                item {
                    val configuration = LocalConfiguration.current
                    val screenWidth = configuration.screenWidthDp.dp

                    CustomTab(
                        modifier = Modifier
                            .padding(top = 12.dp),
                        items = listOf("Тепловоз", "Электровоз"),
                        tabWidth = (screenWidth - 32.dp) / 2,
                        selectedItemIndex = pagerState.currentPage,
                        pagerState = pagerState,
                    )
                }
                item {
                    val stateAccepted = viewModel.acceptedTimeState.value
                    val startAcceptedTime = stateAccepted.startAccepted.time
                    val endAcceptedTime = stateAccepted.endAccepted.time

                    val startAcceptedCalendar = Calendar.getInstance()
                    startAcceptedTime?.let {
                        startAcceptedCalendar.timeInMillis = it
                    }

                    val endAcceptedCalendar = Calendar.getInstance()
                    endAcceptedTime?.let {
                        endAcceptedCalendar.timeInMillis = it
                    }

                    val startAcceptedTimePicker = TimePickerDialog(
                        LocalContext.current,
                        { _, h: Int, m: Int ->
                            startAcceptedCalendar[Calendar.HOUR_OF_DAY] = h
                            startAcceptedCalendar[Calendar.MINUTE] = m
                            startAcceptedCalendar[Calendar.SECOND] = 0
                            startAcceptedCalendar[Calendar.MILLISECOND] = 0
                            viewModel.createEventAccepted(
                                AcceptedEvent.EnteredStartAccepted(
                                    startAcceptedCalendar.timeInMillis
                                )
                            )
                            viewModel.createEventAccepted(AcceptedEvent.FocusChange(AcceptedType.START))
                        },
                        startAcceptedCalendar[Calendar.HOUR_OF_DAY],
                        startAcceptedCalendar[Calendar.MINUTE],
                        true
                    )

                    val startAcceptedDatePicker = DatePickerDialog(
                        LocalContext.current,
                        { _, y: Int, m: Int, d: Int ->
                            startAcceptedCalendar[Calendar.YEAR] = y
                            startAcceptedCalendar[Calendar.MONTH] = m
                            startAcceptedCalendar[Calendar.DAY_OF_MONTH] = d
                            startAcceptedTimePicker.show()
                        },
                        startAcceptedCalendar[Calendar.YEAR],
                        startAcceptedCalendar[Calendar.MONTH],
                        startAcceptedCalendar[Calendar.DAY_OF_MONTH]
                    )

                    val endAcceptedTimePicker = TimePickerDialog(
                        LocalContext.current,
                        { _, h: Int, m: Int ->
                            endAcceptedCalendar[Calendar.HOUR_OF_DAY] = h
                            endAcceptedCalendar[Calendar.MINUTE] = m
                            endAcceptedCalendar[Calendar.SECOND] = 0
                            endAcceptedCalendar[Calendar.MILLISECOND] = 0
                            viewModel.createEventAccepted(
                                AcceptedEvent.EnteredEndAccepted(
                                    endAcceptedCalendar.timeInMillis
                                )
                            )
                            viewModel.createEventAccepted(AcceptedEvent.FocusChange(AcceptedType.END))
                        },
                        endAcceptedCalendar[Calendar.HOUR_OF_DAY],
                        endAcceptedCalendar[Calendar.MINUTE],
                        true
                    )

                    val endAcceptedDatePicker = DatePickerDialog(
                        LocalContext.current,
                        { _, y: Int, m: Int, d: Int ->
                            endAcceptedCalendar[Calendar.YEAR] = y
                            endAcceptedCalendar[Calendar.MONTH] = m
                            endAcceptedCalendar[Calendar.DAY_OF_MONTH] = d
                            endAcceptedTimePicker.show()
                        },
                        endAcceptedCalendar[Calendar.YEAR],
                        endAcceptedCalendar[Calendar.MONTH],
                        endAcceptedCalendar[Calendar.DAY_OF_MONTH]
                    )

                    Column(
                        modifier = Modifier
                            .padding(top = 32.dp)
                            .border(
                                width = 1.dp,
                                shape = ShapeBackground.small,
                                color = MaterialTheme.colors.secondary
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
                                    style = Typography.caption.copy(color = Color.Red),
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
                                style = Typography.body1
                            )

                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clickable {
                                            startAcceptedDatePicker.show()
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
                                        style = Typography.body1,
                                        color = setTextColor(startAcceptedTime)
                                    )
                                }
                                Text(" - ")
                                Box(
                                    modifier = Modifier
                                        .padding(18.dp)
                                        .clickable {
                                            endAcceptedDatePicker.show()
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
                                        style = Typography.body1,
                                        color = setTextColor(endAcceptedTime)
                                    )
                                }
                            }
                        }
                    }
                }
                item {
                    val stateDelivery = viewModel.deliveryTimeState.value
                    val startDeliveryTime = stateDelivery.startDelivered.time
                    val endDeliveryTime = stateDelivery.endDelivered.time

                    val startDeliveryCalendar = Calendar.getInstance()
                    startDeliveryTime?.let {
                        startDeliveryCalendar.timeInMillis = it
                    }

                    val endDeliveryCalendar = Calendar.getInstance()
                    endDeliveryTime?.let {
                        endDeliveryCalendar.timeInMillis = it
                    }

                    val startDeliveryTimePicker = TimePickerDialog(
                        LocalContext.current,
                        { _, h: Int, m: Int ->
                            startDeliveryCalendar[Calendar.HOUR_OF_DAY] = h
                            startDeliveryCalendar[Calendar.MINUTE] = m
                            startDeliveryCalendar[Calendar.SECOND] = 0
                            startDeliveryCalendar[Calendar.MILLISECOND] = 0
                            viewModel.createEventDelivery(
                                DeliveryEvent.EnteredStartDelivery(
                                    startDeliveryCalendar.timeInMillis
                                )
                            )
                            viewModel.createEventDelivery(DeliveryEvent.FocusChange(DeliveredType.START))
                        },
                        startDeliveryCalendar[Calendar.HOUR_OF_DAY],
                        startDeliveryCalendar[Calendar.MINUTE],
                        true
                    )

                    val startDeliveryDatePicker = DatePickerDialog(
                        LocalContext.current,
                        { _, y: Int, m: Int, d: Int ->
                            startDeliveryCalendar[Calendar.YEAR] = y
                            startDeliveryCalendar[Calendar.MONTH] = m
                            startDeliveryCalendar[Calendar.DAY_OF_MONTH] = d
                            startDeliveryTimePicker.show()
                        },
                        startDeliveryCalendar[Calendar.YEAR],
                        startDeliveryCalendar[Calendar.MONTH],
                        startDeliveryCalendar[Calendar.DAY_OF_MONTH]
                    )

                    val endDeliveryTimePicker = TimePickerDialog(
                        LocalContext.current,
                        { _, h: Int, m: Int ->
                            endDeliveryCalendar[Calendar.HOUR_OF_DAY] = h
                            endDeliveryCalendar[Calendar.MINUTE] = m
                            endDeliveryCalendar[Calendar.SECOND] = 0
                            endDeliveryCalendar[Calendar.MILLISECOND] = 0
                            viewModel.createEventDelivery(
                                DeliveryEvent.EnteredEndDelivery(
                                    endDeliveryCalendar.timeInMillis
                                )
                            )
                            viewModel.createEventDelivery(DeliveryEvent.FocusChange(DeliveredType.END))
                        },
                        endDeliveryCalendar[Calendar.HOUR_OF_DAY],
                        endDeliveryCalendar[Calendar.MINUTE],
                        true
                    )

                    val endDeliveryDatePicker = DatePickerDialog(
                        LocalContext.current,
                        { _, y: Int, m: Int, d: Int ->
                            endDeliveryCalendar[Calendar.YEAR] = y
                            endDeliveryCalendar[Calendar.MONTH] = m
                            endDeliveryCalendar[Calendar.DAY_OF_MONTH] = d
                            endDeliveryTimePicker.show()
                        },
                        endDeliveryCalendar[Calendar.YEAR],
                        endDeliveryCalendar[Calendar.MONTH],
                        endDeliveryCalendar[Calendar.DAY_OF_MONTH]
                    )

                    Column(
                        modifier = Modifier
                            .padding(top = 12.dp)
                            .border(
                                width = 1.dp,
                                shape = ShapeBackground.small,
                                color = MaterialTheme.colors.secondary
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
                                    style = Typography.caption.copy(color = Color.Red),
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
                                style = Typography.body1
                            )

                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clickable {
                                            startDeliveryDatePicker.show()
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
                                        style = Typography.body1,
                                        color = setTextColor(startDeliveryTime)
                                    )
                                }
                                Text(" - ")
                                Box(
                                    modifier = Modifier
                                        .padding(18.dp)
                                        .clickable {
                                            endDeliveryDatePicker.show()
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
                                        style = Typography.body1,
                                        color = setTextColor(endDeliveryTime)
                                    )
                                }
                            }
                        }
                    }
                }
                when (pagerState.currentPage) {
                    0 -> {
                        val list = viewModel.dieselSectionListState
                        val revealedSectionIds = viewModel.revealedItemDieselSectionIdsList
                        itemsIndexed(
                            items = list,
                            key = { _, item -> item.sectionId }
                        ) { index, item ->
                            if (index == 0) {
                                SecondarySpacer()
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
                                    onDelete = { viewModel.removeDieselSection(item) }
                                )
                                DraggableDieselItem(
                                    item = item,
                                    index = index,
                                    viewModel = viewModel,
                                    coefficientState = coefficientState,
                                    refuelState = refuelState,
                                    openSheet = openSheet,
                                    isRevealed = revealedSectionIds.contains(item.sectionId),
                                    onCollapse = { viewModel.onCollapsedDieselSection(item.sectionId) },
                                    onExpand = { viewModel.onExpandedDieselSection(item.sectionId) },
                                )
                            }

                        }
                    }
                    1 -> {
                        val list = viewModel.electricSectionListState
                        val revealedSectionIds = viewModel.revealedItemElectricSectionIdsList
                        itemsIndexed(
                            items = list,
                            key = { _, item -> item.sectionId }
                        ) { index, item ->
                            if (index == 0) {
                                SecondarySpacer()
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
                                    onDelete = { viewModel.removeElectricSection(item) }
                                )
                                DraggableElectricItem(
                                    item = item,
                                    isRevealed = revealedSectionIds.contains(item.sectionId),
                                    onExpand = { viewModel.onExpandedElectricSection(item.sectionId) },
                                    onCollapse = { viewModel.onCollapsedElectricSection(item.sectionId) },
                                    index = index,
                                    viewModel = viewModel
                                )
                            }
                        }
                    }
                }
                item {
                    ClickableTextTrainDriver(
                        modifier = Modifier.padding(top = 12.dp),
                        text = AnnotatedString("Добавить секцию")
                    ) {
                        when (pagerState.currentPage) {
                            0 -> viewModel.addDieselSection(SectionDiesel())
                            1 -> viewModel.addElectricSection(SectionElectric())
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SheetLayoutLoco(
    sheet: BottomSheetLoco,
    viewModel: AddingLocoViewModel,
    closeSheet: () -> Unit,
    bottomSheetState: ModalBottomSheetState,
    coefficientState: MutableState<Pair<Int, String>>,
    refuelState: MutableState<Pair<Int, String>>
) {
    BottomSheetWithCloseDialog(
        modifier = Modifier
            .fillMaxHeight(0.65f),
        closeSheet = closeSheet
    ) {
        when (sheet) {
            is BottomSheetLoco.CoefficientSheet -> {
                BottomSheetCoefficient(
                    viewModel = viewModel,
                    coefficientData = coefficientState,
                    sheetState = bottomSheetState
                )
            }
            is BottomSheetLoco.RefuelSheet -> {
                BottomSheetRefuel(
                    viewModel = viewModel,
                    refuelData = refuelState,
                    sheetState = bottomSheetState
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BottomSheetRefuel(
    viewModel: AddingLocoViewModel,
    refuelData: MutableState<Pair<Int, String>>,
    sheetState: ModalBottomSheetState
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
            .padding(start = 16.dp, end = 16.dp, top = 16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Экипировка", style = Typography.subtitle1,
            textAlign = TextAlign.Center
        )
        OutlinedTextFieldCustom(
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
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    scope.launch {
                        focusManager.clearFocus()
                        sheetState.hide()
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun BottomSheetCoefficient(
    viewModel: AddingLocoViewModel,
    coefficientData: MutableState<Pair<Int, String>>,
    sheetState: ModalBottomSheetState
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
            .padding(start = 16.dp, end = 16.dp, top = 16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Коэффициент", style = Typography.subtitle1,
            textAlign = TextAlign.Center
        )
        OutlinedTextFieldCustom(
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
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    scope.launch {
                        focusManager.clearFocus()
                        sheetState.hide()
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

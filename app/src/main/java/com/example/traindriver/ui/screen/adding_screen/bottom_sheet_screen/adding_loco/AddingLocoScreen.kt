package com.example.traindriver.ui.screen.adding_screen.bottom_sheet_screen.adding_loco

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.traindriver.R
import com.example.traindriver.domain.entity.Locomotive
import com.example.traindriver.ui.element_screen.OutlinedTextFieldCustom
import com.example.traindriver.ui.screen.adding_screen.*
import com.example.traindriver.ui.screen.adding_screen.bottom_sheet_screen.BottomSheetWithCloseDialog
import com.example.traindriver.ui.screen.adding_screen.custom_tab.CustomTab
import com.example.traindriver.ui.screen.adding_screen.state_holder.*
import com.example.traindriver.ui.screen.viewing_route_screen.element.setTextColor
import com.example.traindriver.ui.theme.ShapeBackground
import com.example.traindriver.ui.theme.ShapeSurface
import com.example.traindriver.ui.theme.TrainDriverTheme
import com.example.traindriver.ui.theme.Typography
import com.example.traindriver.ui.util.DarkLightPreviews
import com.example.traindriver.ui.util.DateAndTimeFormat
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterialApi::class)
@Composable
fun AddingLocoScreen(
    locomotive: Locomotive? = null,
    viewModel: AddingViewModel
) {
    val bottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val coefficientState: MutableState<Pair<Int, String>> = remember {
        mutableStateOf(Pair<Int, String>(0, "0.0"))
    }
    val scope = rememberCoroutineScope()

    val closeSheet: () -> Unit = {
        scope.launch {
            bottomSheetState.hide()
        }
    }

    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetShape = ShapeSurface.medium,
        sheetContent = {
            BottomSheetCoefficient(
                viewModel = viewModel,
                coefficientData = coefficientState,
                closeSheet = closeSheet,
                sheetState = bottomSheetState
            )
        }
    ) {
        ConstraintLayout(
            modifier = Modifier.fillMaxSize(),
        ) {
            val (
                buttonSave, editSeries, editNumber,
                typeLoco, acceptanceBlock, deliveryBlock,
                sectionBlock
            ) = createRefs()
            var number by remember { mutableStateOf(TextFieldValue(locomotive?.number ?: "")) }
            var series by remember { mutableStateOf(TextFieldValue(locomotive?.series ?: "")) }
            Text(
                modifier = Modifier
                    .constrainAs(buttonSave) {
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                    }
                    .clickable {
                        viewModel.addLocomotive(Locomotive(series = "ВЛ15", number = "033"))
                    }
                    .padding(end = 32.dp, top = 16.dp),
                text = "Сохранить",
                style = Typography.button.copy(color = MaterialTheme.colors.secondaryVariant))

            OutlinedTextFieldCustom(
                modifier = Modifier
                    .constrainAs(editSeries) {
                        start.linkTo(parent.start)
                        end.linkTo(editNumber.start)
                        top.linkTo(buttonSave.bottom)
                        width = Dimension.fillToConstraints
                    }
                    .padding(top = 32.dp, start = 16.dp, end = 8.dp),
                value = series,
                labelText = "Серия",
                onValueChange = { series = it }
            )

            OutlinedTextFieldCustom(
                modifier = Modifier
                    .constrainAs(editNumber) {
                        start.linkTo(editSeries.end)
                        end.linkTo(parent.end)
                        top.linkTo(buttonSave.bottom)
                        width = Dimension.fillToConstraints
                    }
                    .padding(top = 32.dp, end = 16.dp, start = 8.dp),
                value = number,
                labelText = "Номер",
                onValueChange = { number = it }
            )

            val configuration = LocalConfiguration.current
            val screenWidth = configuration.screenWidthDp.dp
            val pagerState = rememberPagerState(pageCount = 2, initialPage = 0)

            CustomTab(
                modifier = Modifier
                    .constrainAs(typeLoco) {
                        top.linkTo(editNumber.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp),
                items = listOf("Тепловоз", "Электровоз"),
                tabWidth = (screenWidth - 32.dp) / 2,
                selectedItemIndex = pagerState.currentPage,
                pagerState = pagerState,
            )

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
                    .padding(start = 16.dp, end = 16.dp, top = 32.dp)
                    .constrainAs(acceptanceBlock) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(typeLoco.bottom)
                        width = Dimension.fillToConstraints
                    }
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

            val stateDelivery = viewModel.deliveryTimeState.value
            val startDeliveryTime = stateDelivery.startDelivered.time
            val endDeliveryTime = stateDelivery.endDelivered.time

            val startDeliveryCalendar = Calendar.getInstance()
            startDeliveryTime?.let {
                startAcceptedCalendar.timeInMillis = it
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
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                    .constrainAs(deliveryBlock) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(acceptanceBlock.bottom)
                        width = Dimension.fillToConstraints
                    }
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

            SectionPager(
                modifier = Modifier
                    .constrainAs(sectionBlock) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(deliveryBlock.bottom)
                        width = Dimension.fillToConstraints
                    }
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp),
                pagerState = pagerState,
                viewModel = viewModel,
                bottomSheetState = bottomSheetState,
                coefficientState = coefficientState
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun BottomSheetCoefficient(
    viewModel: AddingViewModel,
    coefficientData: MutableState<Pair<Int, String>>,
    closeSheet: () -> Unit,
    sheetState: ModalBottomSheetState
) {
    BottomSheetWithCloseDialog(
        modifier = Modifier.fillMaxHeight(0.65f),
        closeSheet = closeSheet
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
}

@Composable
@DarkLightPreviews
private fun AddingLocoPreviews() {
    TrainDriverTheme {
        AddingLocoScreen(viewModel = viewModel())
    }
}

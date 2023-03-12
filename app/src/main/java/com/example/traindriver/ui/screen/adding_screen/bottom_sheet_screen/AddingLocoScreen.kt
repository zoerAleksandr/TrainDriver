package com.example.traindriver.ui.screen.adding_screen.bottom_sheet_screen

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.traindriver.domain.entity.Locomotive
import com.example.traindriver.ui.screen.adding_screen.*
import com.example.traindriver.ui.screen.adding_screen.custom_tab.CustomTab
import com.example.traindriver.ui.screen.viewing_route_screen.element.setTextColor
import com.example.traindriver.ui.theme.ShapeBackground
import com.example.traindriver.ui.theme.TrainDriverTheme
import com.example.traindriver.ui.theme.Typography
import com.example.traindriver.ui.util.DarkLightPreviews
import com.example.traindriver.ui.util.DateAndTimeFormat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AddingLocoScreen(
    locomotive: Locomotive? = null,
    addingLocomotive: (Locomotive) -> Unit,
    viewModel: AddingViewModel
) {
    ConstraintLayout(
        modifier = Modifier.fillMaxSize(),
    ) {
        val (buttonSave, editSeries, editNumber, typeLoco, acceptanceBlock, deliveryBlock) = createRefs()
        var number by remember { mutableStateOf(TextFieldValue(locomotive?.number ?: "")) }
        var series by remember { mutableStateOf(TextFieldValue(locomotive?.series ?: "")) }

        Text(
            modifier = Modifier
                .constrainAs(buttonSave) {
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                }
                .clickable {
                    addingLocomotive.invoke(Locomotive(series = "ВЛ15", number = "033"))
                }
                .padding(end = 32.dp, top = 16.dp),
            text = "Сохранить",
            style = Typography.button.copy(color = MaterialTheme.colors.secondaryVariant))

        OutlinedTextField(
            modifier = Modifier
                .constrainAs(editSeries) {
                    start.linkTo(parent.start)
                    end.linkTo(editNumber.start)
                    top.linkTo(buttonSave.bottom)
                    width = Dimension.fillToConstraints
                }
                .padding(top = 32.dp, start = 16.dp, end = 8.dp),
            value = series,
            label = {
                Text(text = "Серия", style = Typography.body1)
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = MaterialTheme.colors.primary,
                unfocusedBorderColor = MaterialTheme.colors.secondary
            ),
            onValueChange = { series = it }
        )

        OutlinedTextField(
            modifier = Modifier
                .constrainAs(editNumber) {
                    start.linkTo(editSeries.end)
                    end.linkTo(parent.end)
                    top.linkTo(buttonSave.bottom)
                    width = Dimension.fillToConstraints
                }
                .padding(top = 32.dp, end = 16.dp, start = 8.dp),
            value = number,
            label = {
                Text("Номер", style = Typography.body1)
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = MaterialTheme.colors.primary,
                unfocusedBorderColor = MaterialTheme.colors.secondary
            ),
            onValueChange = { number = it }
        )

        val (selected, setSelected) = remember {
            mutableStateOf(0)
        }
        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp.dp

        CustomTab(
            modifier = Modifier
                .constrainAs(typeLoco) {
                    top.linkTo(editNumber.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .padding(16.dp),
            items = listOf("Тепловоз", "Электровоз"),
            tabWidth = (screenWidth - 32.dp) / 2,
            selectedItemIndex = selected,
            onClick = setSelected,
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
            LocalContext.current, { _, h: Int, m: Int ->
                endAcceptedCalendar[Calendar.HOUR_OF_DAY] = h
                endAcceptedCalendar[Calendar.MINUTE] = m
                endAcceptedCalendar[Calendar.SECOND] = 0
                endAcceptedCalendar[Calendar.MILLISECOND] = 0
                viewModel.createEventAccepted(AcceptedEvent.EnteredEndAccepted(endAcceptedCalendar.timeInMillis))
                viewModel.createEventAccepted(AcceptedEvent.FocusChange(AcceptedType.END))
            }, endAcceptedCalendar[Calendar.HOUR_OF_DAY], endAcceptedCalendar[Calendar.MINUTE], true
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


        Row(modifier = Modifier
            .padding(horizontal = 16.dp)
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

@Composable
@DarkLightPreviews
private fun AddingLocoPreviews() {
    TrainDriverTheme {
        AddingLocoScreen(addingLocomotive = {}, viewModel = viewModel())
    }
}

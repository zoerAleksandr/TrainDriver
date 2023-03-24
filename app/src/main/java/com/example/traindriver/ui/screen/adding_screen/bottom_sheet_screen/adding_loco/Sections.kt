package com.example.traindriver.ui.screen.adding_screen.bottom_sheet_screen.adding_loco

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.traindriver.R
import com.example.traindriver.domain.entity.Calculation
import com.example.traindriver.domain.entity.SectionElectric
import com.example.traindriver.ui.element_screen.OutlinedTextFieldCustom
import com.example.traindriver.ui.screen.adding_screen.AddingViewModel
import com.example.traindriver.ui.screen.adding_screen.state_holder.DieselSectionEvent
import com.example.traindriver.ui.screen.adding_screen.state_holder.DieselSectionFormState
import com.example.traindriver.ui.theme.ShapeBackground
import com.example.traindriver.ui.theme.Typography
import com.example.traindriver.ui.util.ClickableTextTrainDriver
import com.example.traindriver.ui.util.double_util.*
import kotlinx.coroutines.launch

@Composable
fun DieselSectionItem(
    modifier: Modifier = Modifier,
    index: Int,
    item: DieselSectionFormState,
    viewModel: AddingViewModel,
    coefficientState: MutableState<Pair<Int, String>>,
    refuelState: MutableState<Pair<Int, String>>,
    openSheet: (BottomSheetLoco) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    val acceptedText = item.accepted.data ?: ""
    val accepted = acceptedText.toDoubleOrNull()
    val deliveryText = item.delivery.data ?: ""
    val delivery = deliveryText.toDoubleOrNull()
    val coefficient = item.coefficient.data?.toDoubleOrNull()
    val refuel = item.refuel.data?.toDoubleOrNull()
    val acceptedInKilo = accepted.times(coefficient)
    val deliveryInKilo = delivery.times(coefficient)
    val result = Calculation.getTotalFuelConsumption(accepted, delivery, refuel)
    val resultInKilo = Calculation.getTotalFuelInKiloConsumption(result, coefficient)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colors.primaryVariant,
                shape = ShapeBackground.small
            ),
        horizontalAlignment = Alignment.End
    ) {
        fun maskInKilo(string: String?): String? {
            return string?.let {
                "$it кг"
            }
        }

        fun maskInLiter(string: String?): String? {
            return string?.let {
                "$it л"
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.padding(start = 16.dp),
                text = "${index + 1}",
                style = Typography.subtitle1.copy(color = MaterialTheme.colors.primary)
            )
            Row(
                modifier = Modifier
                    .clickable {
                        refuelState.value = refuelState.value.copy(
                            first = index, second = refuel?.str() ?: ""
                        )
                        scope.launch {
                            openSheet.invoke(BottomSheetLoco.RefuelSheet)
                        }
                    }
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    modifier = Modifier
                        .size(dimensionResource(id = R.dimen.icon_size))
                        .padding(end = 8.dp),
                    painter = painterResource(id = R.drawable.refuel_icon),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(MaterialTheme.colors.secondaryVariant)
                )
                refuel?.let {
                    Text(
                        text = maskInLiter(it.str()) ?: "",
                        style = Typography.body1.copy(color = MaterialTheme.colors.primary),
                    )
                }
            }
        }

        Row(
            modifier = Modifier.padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            OutlinedTextFieldCustom(
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .weight(0.5f),
                value = acceptedText,
                onValueChange = {
                    viewModel.createEventDieselSection(
                        DieselSectionEvent.EnteredAccepted(
                            index = index, data = it
                        )
                    )
                },
                labelText = "Принял",
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
                )
            )

            OutlinedTextFieldCustom(
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .weight(0.5f),
                value = deliveryText,
                onValueChange = {
                    viewModel.createEventDieselSection(
                        DieselSectionEvent.EnteredDelivery(
                            index = index, data = it
                        )
                    )
                },
                labelText = "Сдал",
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

        if (item.accepted.data != null || item.delivery.data != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    contentAlignment = Alignment.TopStart
                ) {
                    val acceptedInKiloText = rounding(acceptedInKilo, 2)?.str()
                    Text(
                        text = maskInKilo(acceptedInKiloText) ?: "",
                        style = Typography.body1
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp),
                    contentAlignment = Alignment.TopStart
                ) {
                    val deliveryInKiloText = rounding(deliveryInKilo, 2)?.str()
                    Text(
                        text = maskInKilo(deliveryInKiloText) ?: "",
                        style = Typography.body1
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ClickableTextTrainDriver(
                text = AnnotatedString("k = ${coefficient ?: 0.0}"),
                onClick = {
                    coefficientState.value = coefficientState.value.copy(
                        first = index, second = coefficient?.str() ?: ""
                    )
                    scope.launch {
                        openSheet.invoke(BottomSheetLoco.CoefficientSheet)
                    }
                })

            if (result != null) {
                val resultInLiterText = maskInLiter(result.str())
                val resultInKiloText = maskInKilo(rounding(resultInKilo, 2)?.str())
                Text(
                    text = "${resultInLiterText ?: ""} / ${resultInKiloText ?: ""}",
                    style = Typography.body1
                )
            }
        }
    }
}

@Composable
fun ElectricSectionItem(section: SectionElectric) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colors.primaryVariant,
                shape = ShapeBackground.small
            )
    ) {
        Text(text = "I am Item Electric Section")
    }
}
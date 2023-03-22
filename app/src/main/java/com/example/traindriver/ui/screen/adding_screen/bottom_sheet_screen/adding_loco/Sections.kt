package com.example.traindriver.ui.screen.adding_screen.bottom_sheet_screen.adding_loco

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.example.traindriver.R
import com.example.traindriver.domain.entity.SectionDiesel
import com.example.traindriver.domain.entity.SectionElectric
import com.example.traindriver.ui.element_screen.OutlinedTextFieldCustom
import com.example.traindriver.ui.screen.adding_screen.AddingViewModel
import com.example.traindriver.ui.screen.adding_screen.state_holder.DieselSectionEvent
import com.example.traindriver.ui.screen.adding_screen.state_holder.DieselSectionFormState
import com.example.traindriver.ui.theme.ShapeBackground
import com.example.traindriver.ui.theme.Typography
import com.example.traindriver.ui.util.ClickableTextTrainDriver
import com.example.traindriver.ui.util.double_util.rounding
import com.example.traindriver.ui.util.double_util.str
import com.example.traindriver.ui.util.double_util.times
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import kotlinx.coroutines.launch
import com.example.traindriver.ui.util.double_util.minus


@OptIn(ExperimentalPagerApi::class)
@Composable
fun SectionPager(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    viewModel: AddingViewModel,
    coefficientState: MutableState<Pair<Int, String>>,
    refuelState: MutableState<Pair<Int, String>>,
    openSheet: (BottomSheetLoco) -> Unit
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.End) {
        when (pagerState.currentPage) {
            0 -> DieselSectionList(
                viewModel = viewModel,
                coefficientState = coefficientState,
                refuelState = refuelState,
                openSheet = openSheet
            )
            1 -> ElectricSectionList(viewModel.electricSectionListState.value)
        }

        ClickableTextTrainDriver(
            modifier = Modifier.padding(top = 8.dp), text = AnnotatedString("Добавить секцию")
        ) {
            when (pagerState.currentPage) {
                0 -> viewModel.addDieselSection(SectionDiesel())
                1 -> viewModel.addElectricSection()
            }
        }
    }
}

@Composable
fun DieselSectionList(
    viewModel: AddingViewModel,
    coefficientState: MutableState<Pair<Int, String>>,
    refuelState: MutableState<Pair<Int, String>>,
    openSheet: (BottomSheetLoco) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
    ) {
        val state = viewModel.dieselSectionListState
        itemsIndexed(state) { index, item ->
            DieselSectionItem(
                index = index,
                item = item,
                viewModel = viewModel,
                coefficientState = coefficientState,
                refuelState = refuelState,
                openSheet = openSheet
            )
        }
    }
}

@Composable
fun DieselSectionItem(
    index: Int,
    item: DieselSectionFormState,
    viewModel: AddingViewModel,
    coefficientState: MutableState<Pair<Int, String>>,
    refuelState: MutableState<Pair<Int, String>>,
    openSheet: (BottomSheetLoco) -> Unit
) {
    val scope = rememberCoroutineScope()
    val accepted = item.accepted.data
    val delivery = item.delivery.data
    val coefficient = item.coefficient.data
    val acceptedInKilo = accepted.times(coefficient)
    val deliveryInKilo = delivery.times(coefficient)
    val result = accepted - delivery
    val resultInKilo = acceptedInKilo - deliveryInKilo
    val refuel = item.refuel.data

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colors.primaryVariant,
                shape = ShapeBackground.small
            ), horizontalAlignment = Alignment.Start
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
            modifier = Modifier
                .clickable {
                    refuelState.value = refuelState.value.copy(
                        first = index, second = refuel?.str() ?: ""
                    )
                    scope.launch {
                        openSheet.invoke(BottomSheetLoco.RefuelSheet)
                    }
                }
                .fillMaxWidth()
                .padding(end = 16.dp, top = 8.dp),
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
                    text = "${it.str()} л",
                    style = Typography.body1.copy(color = MaterialTheme.colors.primary),
                )
            }
        }

        Row(
            modifier = Modifier.padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val acceptedFuelText = accepted?.str() ?: ""
            val deliveryFuelText = delivery?.str() ?: ""

            OutlinedTextFieldCustom(
                modifier = Modifier
                    .padding(8.dp)
                    .weight(0.5f),
                value = acceptedFuelText,
                onValueChange = {
                    viewModel.createEventDieselSection(
                        DieselSectionEvent.EnteredAccepted(
                            index = index, data = it.toDoubleOrNull()
                        )
                    )
                },
                labelText = "Принял"
            )

            OutlinedTextFieldCustom(
                modifier = Modifier
                    .padding(8.dp)
                    .weight(0.5f),
                value = deliveryFuelText,
                onValueChange = {
                    viewModel.createEventDieselSection(
                        DieselSectionEvent.EnteredDelivery(
                            index = index, data = it.toDoubleOrNull()
                        )
                    )
                },
                labelText = "Сдал"
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
                    contentAlignment = Alignment.CenterStart
                ) {
                    val acceptedInKiloText = rounding(acceptedInKilo, 2)?.str()
                    Text(text = maskInKilo(acceptedInKiloText) ?: "", style = Typography.body1)
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    val deliveryInKiloText = rounding(deliveryInKilo, 2)?.str()
                    Text(text = maskInKilo(deliveryInKiloText) ?: "", style = Typography.body1)
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
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
                val resultInKiloText = maskInKilo(resultInKilo?.str())
                Text(
                    text = "${resultInLiterText ?: ""} / ${resultInKiloText ?: ""}",
                    style = Typography.body1
                )
            }
        }
    }
}

@Composable
fun ElectricSectionList(sectionList: List<SectionElectric>) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
    ) {
        items(sectionList) { item ->
            ElectricSectionItem(item)
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

package com.example.traindriver.ui.screen.adding_screen.bottom_sheet_screen.adding_loco

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.example.traindriver.domain.entity.SectionDiesel
import com.example.traindriver.domain.entity.SectionElectric
import com.example.traindriver.ui.element_screen.OutlinedTextFieldCustom
import com.example.traindriver.ui.screen.adding_screen.AddingViewModel
import com.example.traindriver.ui.screen.adding_screen.state_holder.DieselSectionEvent
import com.example.traindriver.ui.screen.adding_screen.state_holder.DieselSectionFormState
import com.example.traindriver.ui.theme.ShapeBackground
import com.example.traindriver.ui.util.ClickableTextTrainDriver
import com.example.traindriver.ui.util.double_util.str
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import kotlinx.coroutines.launch


@OptIn(ExperimentalPagerApi::class, ExperimentalMaterialApi::class)
@Composable
fun SectionPager(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    viewModel: AddingViewModel,
    scaffoldState: BottomSheetScaffoldState,
    coefficientState: MutableState<Pair<Int, Double?>>
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.End) {
        when (pagerState.currentPage) {
            0 -> DieselSectionList(viewModel, scaffoldState, coefficientState)
            1 -> ElectricSectionList(viewModel.electricSectionListState.value)
        }

        ClickableTextTrainDriver(
            modifier = Modifier.padding(top = 8.dp),
            text = AnnotatedString("Добавить секцию")
        ) {
            when (pagerState.currentPage) {
                0 -> viewModel.addDieselSection(SectionDiesel())
                1 -> viewModel.addElectricSection()
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DieselSectionList(
    viewModel: AddingViewModel,
    scaffoldState: BottomSheetScaffoldState,
    coefficientState: MutableState<Pair<Int, Double?>>) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
    ) {
        val state = viewModel.dieselSectionListState
        itemsIndexed(state) { index, item ->
            DieselSectionItem(index, item, viewModel, scaffoldState, coefficientState)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DieselSectionItem(
    index: Int,
    item: DieselSectionFormState,
    viewModel: AddingViewModel,
    scaffoldState: BottomSheetScaffoldState,
    coefficientState: MutableState<Pair<Int, Double?>>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colors.primaryVariant,
                shape = ShapeBackground.small
            ),
        horizontalAlignment = Alignment.End
    ) {
        val accepted = item.accepted.data
        val delivery = item.delivery.data
        val coefficient = item.coefficient.data

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
                            index = index,
                            data = it.toDoubleOrNull()
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
                            index = index,
                            data = it.toDoubleOrNull()
                        )
                    )
                },
                labelText = "Сдал"
            )

        }
        val scope = rememberCoroutineScope()
        ClickableTextTrainDriver(
            modifier = Modifier.padding(end = 16.dp),
            text = AnnotatedString("k = ${coefficient ?: 0.0}"),
            onClick = {
                coefficientState.value = coefficientState.value.copy(
                    first = index,
                    second = coefficient
                )
                scope.launch {
                    scaffoldState.bottomSheetState.expand()
                }
            }
        )

        if (accepted != null && delivery != null) {
            val result = accepted - delivery
            Text(
                modifier = Modifier.padding(8.dp),
                text = result.str()
            )
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

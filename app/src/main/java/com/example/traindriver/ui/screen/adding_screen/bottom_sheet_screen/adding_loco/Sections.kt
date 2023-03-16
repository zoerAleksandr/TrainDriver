package com.example.traindriver.ui.screen.adding_screen.bottom_sheet_screen.adding_loco

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.traindriver.ui.theme.ShapeBackground
import com.example.traindriver.ui.util.ClickableTextTrainDriver
import com.example.traindriver.ui.util.double_util.str
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState


@OptIn(ExperimentalPagerApi::class)
@Composable
fun SectionPager(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    viewModel: AddingViewModel
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.End) {
        when (pagerState.currentPage) {
            0 -> DieselSectionList(viewModel.dieselSectionListState.value)
            1 -> ElectricSectionList(viewModel.electricSectionListState.value)
        }

        ClickableTextTrainDriver(
            modifier = Modifier.padding(top = 8.dp),
            text = AnnotatedString("Добавить секцию")
        ) {
            when (pagerState.currentPage) {
                0 -> viewModel.addDieselSection()
                1 -> viewModel.addElectricSection()
            }
        }
    }
}

@Composable
fun DieselSectionList(sectionList: List<SectionDiesel>) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
    ) {
        items(sectionList) { item ->
            DieselSectionItem(item)
        }
    }
}

@Composable
fun DieselSectionItem(section: SectionDiesel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colors.primaryVariant,
                shape = ShapeBackground.small
            ),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            var acceptedFuel by remember {
                mutableStateOf(section.acceptedEnergy?.str() ?: "")
            }
            var deliveryFuelText by remember {
                mutableStateOf(section.deliveryEnergy?.str() ?: "")
            }

            OutlinedTextFieldCustom(
                modifier = Modifier
                    .padding(8.dp)
                    .weight(0.5f),
                value = acceptedFuel,
                onValueChange = { acceptedFuel = it },
                labelText = "Принял"
            )

            OutlinedTextFieldCustom(
                modifier = Modifier
                    .padding(8.dp)
                    .weight(0.5f),
                value = deliveryFuelText,
                onValueChange = { deliveryFuelText = it },
                labelText = "Сдал"
            )
        }

//        section.fuelSupply?.let {
//            Text(
//                text = it.str(),
//                modifier = Modifier.padding(start = 8.dp, bottom = 8.dp),
//                style = Typography.body1
//            )
//        }
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

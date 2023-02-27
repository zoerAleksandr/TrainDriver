package com.example.traindriver.ui.screen.viewing_route_screen.element

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.example.traindriver.R
import com.example.traindriver.data.util.ResultState
import com.example.traindriver.domain.entity.*
import com.example.traindriver.ui.element_screen.LoadingElement
import com.example.traindriver.ui.screen.viewing_route_screen.ViewingRouteViewModel
import com.example.traindriver.ui.theme.ShapeBackground
import com.example.traindriver.ui.theme.TrainDriverTheme
import com.example.traindriver.ui.theme.Typography
import com.example.traindriver.ui.util.DarkLightPreviews
import com.example.traindriver.ui.util.DateAndTimeFormat.DEFAULT_TIME_TEXT
import com.example.traindriver.ui.util.DateAndTimeFormat.TIME_FORMAT
import com.example.traindriver.ui.util.EmptyDataText.DEFAULT_ENERGY
import com.example.traindriver.ui.util.EmptyDataText.RESULT_ENERGY
import java.text.SimpleDateFormat
import kotlin.math.pow
import kotlin.math.roundToInt

@Composable
fun LocoScreen(viewModel: ViewingRouteViewModel, navController: NavController) {
    when (val routeState = viewModel.routeState) {
        is ResultState.Loading -> {
            LoadingScreen()
        }
        is ResultState.Success -> routeState.data?.let { route ->
            DataScreen(route, navController)
        }
        is ResultState.Failure -> {
            FailureScreen()
        }
    }
}

@Composable
private fun LoadingScreen() {
    LoadingElement()
}

@Composable
private fun DataScreen(route: Route, navController: NavController) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        itemsIndexed(route.locoList) { index, item ->
            ItemLocomotive(item, navController)
            if (index == route.locoList.size - 1) {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }
}

@Composable
private fun FailureScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = stringResource(id = R.string.route_opening_error), style = Typography.h3)
    }
}

@Composable
fun ItemLocomotive(loco: Locomotive, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp),
        backgroundColor = MaterialTheme.colors.background,
        shape = ShapeBackground.medium,
        elevation = 6.dp
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            val (seriesAndNumber, time, sections) = createRefs()

            Box(modifier = Modifier.constrainAs(seriesAndNumber) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
            }) {
                Row(horizontalArrangement = Arrangement.SpaceAround) {
                    val seriesText = loco.series ?: "xxxx"
                    val numberText = loco.number ?: "000"
                    Text(
                        text = seriesText,
                        color = setTextColor(loco.series),
                        style = Typography.subtitle1
                    )
                    Text(
                        text = " - ",
                        color = setTextColor(loco.number),
                        style = Typography.subtitle1
                    )
                    Text(
                        text = numberText,
                        color = setTextColor(loco.number),
                        style = Typography.subtitle1
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .constrainAs(time) {
                        top.linkTo(seriesAndNumber.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.border(
                        width = 0.5.dp,
                        color = MaterialTheme.colors.secondary,
                        shape = ShapeBackground.medium
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        val timeStartAcceptance = loco.timeStartOfAcceptance?.let { millis ->
                            SimpleDateFormat(TIME_FORMAT).format(millis)
                        } ?: DEFAULT_TIME_TEXT

                        val timeEndAcceptance = loco.timeEndOfAcceptance?.let { millis ->
                            SimpleDateFormat(TIME_FORMAT).format(millis)
                        } ?: DEFAULT_TIME_TEXT

                        Text(
                            text = timeStartAcceptance,
                            color = setTextColor(loco.timeStartOfAcceptance)
                        )
                        Text(
                            text = " - ", color = setTextColor(loco.timeEndOfAcceptance)
                        )
                        Text(
                            text = timeEndAcceptance, color = setTextColor(loco.timeEndOfAcceptance)
                        )
                    }
                }

                Box(
                    modifier = Modifier.border(
                        width = 0.5.dp,
                        color = MaterialTheme.colors.secondary,
                        shape = ShapeBackground.medium
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        val timeStartDelivery = loco.timeStartOfDelivery?.let { millis ->
                            SimpleDateFormat(TIME_FORMAT).format(millis)
                        } ?: DEFAULT_TIME_TEXT

                        val timeEndDelivery = loco.timeEndOfDelivery?.let { millis ->
                            SimpleDateFormat(TIME_FORMAT).format(millis)
                        } ?: DEFAULT_TIME_TEXT

                        Text(
                            text = timeStartDelivery,
                            color = setTextColor(loco.timeStartOfAcceptance)
                        )
                        Text(
                            text = " - ", color = setTextColor(loco.timeEndOfAcceptance)
                        )
                        Text(
                            text = timeEndDelivery, color = setTextColor(loco.timeEndOfAcceptance)
                        )
                    }
                }
            }
            Column(modifier = Modifier
                .constrainAs(sections) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(time.bottom)
                }
                .padding(top = 8.dp)) {
                loco.sectionList.forEach { item: Section ->
                    ItemSection(section = item)
                }
                GeneralResult(
                    modifier = Modifier.padding(top = 8.dp, end = 16.dp, start = 16.dp),
                    loco = loco
                )
            }
        }
    }
}

operator fun Double.plus(other: Double?): Double =
    if (other != null) {
        this + other
    } else {
        this
    }

fun Double.str(): String {
    return if (this % 1.0 == 0.0) {
        this.toString().dropLast(2)
    } else {
        this.toString()
    }
}

@Composable
fun GeneralResult(modifier: Modifier, loco: Locomotive) {
    when (loco.type) {
        true -> {
            var totalConsumption = 0.0
            loco.sectionList.forEach { section: Section ->
                totalConsumption += section.getConsumption()
            }

            var totalRecovery = 0.0
            loco.sectionList.forEach { section: Section ->
                if (section is SectionElectric) {
                    totalRecovery += section.getRecoveryResult()
                }
            }
            Box(
                modifier = modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                if (totalConsumption != 0.0) {
                    Text(
                        text = "${totalConsumption.str()} / ${totalRecovery.str()}",
                        style = Typography.body2
                    )
                }
            }
        }
        false -> {
            Box(
                modifier = modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                var totalConsumption = 0.0
                loco.sectionList.forEach { section: Section ->
                    totalConsumption += section.getConsumption()
                }

                var totalConsumptionInKilo = 0.0
                loco.sectionList.forEach { section ->
                    if (section is SectionDiesel) {
                        totalConsumptionInKilo += section.getConsumptionInKilo()
                    }
                }
                if (totalConsumption != 0.0) {
                    Text(
                        text = "${totalConsumption.str()}л / ${totalConsumptionInKilo.str()}кг",
                        style = Typography.body2
                    )
                }
            }
        }
    }
}

@Composable
fun ItemSection(section: Section) {
    when (section) {
        is SectionElectric -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
                    .border(
                        width = 0.5.dp,
                        color = MaterialTheme.colors.secondary,
                        shape = ShapeBackground.medium
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 16.dp),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.wrapContentWidth(),
                        ) {
                            val acceptedText = section.acceptedEnergy?.str() ?: DEFAULT_ENERGY
                            val deliveryText = section.deliveryEnergy?.str() ?: DEFAULT_ENERGY
                            Text(text = acceptedText, color = setTextColor(section.acceptedEnergy))
                            Text(text = " - ", color = setTextColor(section.deliveryEnergy))
                            Text(text = deliveryText, color = setTextColor(section.deliveryEnergy))
                        }
                        val resultText = section.getConsumption()?.str() ?: RESULT_ENERGY
                        Text(text = resultText, color = setTextColor(section.getConsumption()))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.wrapContentWidth(),
                        ) {
                            section.acceptedRecovery?.let { value ->
                                Text(text = value.str(), color = setTextColor(value))
                            }
                            section.deliveryRecovery?.let { value ->
                                Text(text = " - ", color = setTextColor(value))
                                Text(text = value.str(), color = setTextColor(value))
                            }
                        }
                        section.getRecoveryResult()?.let { value ->
                            Text(text = value.str(), color = setTextColor(value))
                        }
                    }
                }
            }
        }
        is SectionDiesel -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
                    .border(
                        width = 0.5.dp,
                        color = MaterialTheme.colors.secondary,
                        shape = ShapeBackground.medium
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 16.dp),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.wrapContentWidth(),
                        ) {
                            val acceptedText = section.acceptedEnergy?.str() ?: DEFAULT_ENERGY
                            val deliveryText = section.deliveryEnergy?.str() ?: DEFAULT_ENERGY
                            Text(
                                text = acceptedText,
                                color = setTextColor(section.acceptedEnergy),
                                style = Typography.body1
                            )
                            Text(
                                text = " - ",
                                color = setTextColor(section.deliveryEnergy),
                                style = Typography.body1
                            )
                            Text(
                                text = deliveryText,
                                color = setTextColor(section.deliveryEnergy),
                                style = Typography.body1
                            )
                        }
                        val resultText = "${section.getConsumption()?.str() ?: RESULT_ENERGY} л"
                        Text(
                            text = resultText,
                            color = setTextColor(section.getConsumption()),
                            style = Typography.body1
                        )
                    }

                    if (section.acceptedEnergy != null || section.deliveryEnergy != null) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.wrapContentWidth(),
                                ) {
                                    val acceptedText =
                                        section.acceptedInKilo?.str() ?: DEFAULT_ENERGY
                                    val deliveryText =
                                        section.deliveryInKilo?.str() ?: DEFAULT_ENERGY
                                    Text(
                                        text = acceptedText,
                                        color = setTextColor(section.acceptedInKilo),
                                        style = Typography.body1
                                    )
                                    Text(
                                        text = " - ",
                                        color = setTextColor(section.deliveryInKilo),
                                        style = Typography.body1
                                    )
                                    Text(
                                        text = deliveryText,
                                        color = setTextColor(section.deliveryInKilo),
                                        style = Typography.body1
                                    )
                                }
                                val r = section.getConsumptionInKilo()?.let { rounding(it, 2) }
                                val resultText = "${r?.str() ?: RESULT_ENERGY} кг"

                                Text(
                                    text = resultText,
                                    color = setTextColor(section.getConsumptionInKilo()),
                                    style = Typography.body1
                                )
                            }
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                section.coefficient?.let { coefficient ->
                                    Text(text = "k = $coefficient", style = Typography.body2)
                                }
                            }
                        }
                    }
                    section.fuelSupply?.let { fuel ->
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(text = "Снабжение", style = Typography.body2)
                            Text(text = "${fuel.str()}л", style = Typography.body2)
                        }
                    }
                }
            }
        }
    }
}

fun rounding(value: Double, count: Int) = (value * 10.0.pow(count)).roundToInt() / 10.0.pow(count)

@Composable
@ReadOnlyComposable
fun setTextColor(any: Any?): Color = if (any == null) {
    MaterialTheme.colors.primaryVariant
} else {
    MaterialTheme.colors.primary
}

//@Composable
//@DarkLightPreviews
//private fun LocoScreenPrev() {
//    TrainDriverTheme {
//        LocoScreen(viewModel())
//    }
//}

@Composable
@DarkLightPreviews
private fun ItemSectionPrev() {
    TrainDriverTheme {
        ItemSection(
            SectionElectric(acceptedEnergy = 133032.0, deliveryEnergy = 113064.3)
        )
    }
}
package com.example.traindriver.ui.screen.viewing_route_screen.element

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
fun LocoScreen(viewModel: ViewingRouteViewModel) {
    when (val routeState = viewModel.routeState) {
        is ResultState.Loading -> {
            LoadingScreen()
        }
        is ResultState.Success -> routeState.data?.let { route ->
            DataScreen(route)
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
private fun DataScreen(route: Route) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(route.locoList) { item: Locomotive ->
            ItemLocomotive(item)
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
fun ItemLocomotive(loco: Locomotive) {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, top = 8.dp),
        backgroundColor = MaterialTheme.colors.background,
//        border = BorderStroke(0.5.dp, MaterialTheme.colors.secondary),
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
                    Text(text = seriesText, color = setTextColor(loco.series))
                    Text(text = " - ", color = setTextColor(loco.number))
                    Text(text = numberText, color = setTextColor(loco.number))
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
                GeneralResult(modifier = Modifier.padding(top = 8.dp, end = 16.dp), loco = loco)
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
                    Text(text = "${totalConsumption.str()} / ${totalRecovery.str()}")
                }
            }
        }
        false -> {

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
                            Text(text = section.acceptedEnergy.toString())
                            Text(text = " - ")
                            Text(text = section.deliveryEnergy.toString())
                        }
                        Text(text = section.getConsumption().toString())
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.wrapContentWidth(),
                        ) {
                            Text(text = section.acceptedInKilo.toString())
                            Text(text = " - ")
                            Text(text = section.deliveryInKilo.toString())
                        }
                        val r = section.getConsumptionInKilo()?.let { rounding(it, 2) }
                        Text(text = r.toString())
                    }

                    section.coefficient?.let { k ->
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(text = "k = $k", color = MaterialTheme.colors.primaryVariant)
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
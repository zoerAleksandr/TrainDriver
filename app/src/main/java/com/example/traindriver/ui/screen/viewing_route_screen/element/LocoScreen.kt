package com.example.traindriver.ui.screen.viewing_route_screen.element

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.traindriver.R
import com.example.traindriver.data.util.ResultState
import com.example.traindriver.domain.entity.*
import com.example.traindriver.ui.element_screen.LoadingElement
import com.example.traindriver.ui.screen.Screen
import com.example.traindriver.ui.screen.viewing_route_screen.ViewingRouteViewModel
import com.example.traindriver.ui.theme.ColorClickableText
import com.example.traindriver.ui.theme.ShapeBackground
import com.example.traindriver.ui.theme.TrainDriverTheme
import com.example.traindriver.ui.theme.Typography
import com.example.traindriver.ui.util.DarkLightPreviews
import com.example.traindriver.ui.util.DateAndTimeFormat.DEFAULT_TIME_TEXT
import com.example.traindriver.ui.util.DateAndTimeFormat.TIME_FORMAT
import com.example.traindriver.ui.util.EmptyDataText.DEFAULT_ENERGY
import com.example.traindriver.ui.util.EmptyDataText.RESULT_ENERGY
import com.example.traindriver.ui.util.double_util.plus
import com.example.traindriver.ui.util.double_util.rounding
import com.example.traindriver.ui.util.double_util.str
import java.text.SimpleDateFormat

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
            if (index == route.locoList.lastIndex) {
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
        backgroundColor = MaterialTheme.colors.surface,
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
                    ItemSection(section = item, navController = navController)
                }
                GeneralResult(
                    modifier = Modifier.padding(top = 8.dp, end = 16.dp, start = 16.dp),
                    loco = loco
                )
            }
        }
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
                        style = Typography.body2,
                        color = MaterialTheme.colors.primary
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
                        style = Typography.body2,
                        color = MaterialTheme.colors.primary
                    )
                }
            }
        }
    }
}

@Composable
fun ItemSection(section: Section, navController: NavController) {
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
                                Text(text = value.str(),
                                    style = Typography.body1.copy(color = setTextColor(value)))
                            }
                            section.deliveryRecovery?.let { value ->
                                Text(text = " - ",
                                    style = Typography.body1.copy(color = setTextColor(value)))
                                Text(text = value.str(),
                                    style = Typography.body1.copy(color = setTextColor(value)))
                            }
                        }
                        section.getRecoveryResult()?.let { value ->
                            Text(text = value.str(),
                                style = Typography.body1.copy(color = setTextColor(value)))
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
                                val linkText = buildAnnotatedString {
                                    val value = section.coefficient?.toString() ?: "0.00"
                                    val text = "k = $value"

                                    val startIndex = startIndexLastWord(text)
                                    val endIndex = text.length

                                    append(text)
                                    addStyle(
                                        SpanStyle(
                                            color = ColorClickableText,
                                            textDecoration = TextDecoration.Underline
                                        ), start = startIndex, end = endIndex
                                    )

                                    addStringAnnotation(
                                        tag = LINK_TO_SETTING,
                                        annotation = Screen.Setting.route,
                                        start = startIndex,
                                        end = endIndex
                                    )
                                }
                                ClickableText(
                                    text = linkText,
                                    style = Typography.body2
                                        .copy(color = setTextColor(section.coefficient))
                                ) {
                                    linkText.getStringAnnotations(LINK_TO_SETTING, it, it)
                                        .firstOrNull()?.let { annotation ->
                                            navController.navigate(annotation.item)
                                        }
                                }
                            }
                        }
                    }
                    section.fuelSupply?.let { fuel ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                modifier = Modifier
                                    .size(dimensionResource(id = R.dimen.icon_size))
                                    .padding(end = 8.dp),
                                painter = painterResource(id = R.drawable.refuel_icon),
                                contentDescription = null,
                                colorFilter = ColorFilter.tint(MaterialTheme.colors.secondary)
                            )
                            Text(
                                text = "${fuel.str()}л",
                                style = Typography.body2,
                                color = MaterialTheme.colors.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
@ReadOnlyComposable
fun setTextColor(any: Any?): Color = if (any == null) {
    MaterialTheme.colors.primaryVariant
} else {
    MaterialTheme.colors.primary
}

@Composable
@DarkLightPreviews
private fun ItemSectionPrev() {
    TrainDriverTheme {
        ItemSection(
            SectionElectric(acceptedEnergy = 133032.0, deliveryEnergy = 113064.3),
            rememberNavController()
        )
    }
}
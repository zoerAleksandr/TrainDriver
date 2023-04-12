package com.example.traindriver.ui.screen.viewing_route_screen.element

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.traindriver.R
import com.example.traindriver.data.util.ResultState
import com.example.traindriver.domain.entity.*
import com.example.traindriver.ui.element_screen.LoadingElement
import com.example.traindriver.ui.screen.Screen
import com.example.traindriver.ui.screen.viewing_route_screen.RouteResponse
import com.example.traindriver.ui.theme.ColorClickableText
import com.example.traindriver.ui.theme.ShapeBackground
import com.example.traindriver.ui.theme.TrainDriverTheme
import com.example.traindriver.ui.theme.Typography
import com.example.traindriver.ui.util.Constants.DURATION_CROSSFADE
import com.example.traindriver.ui.util.DarkLightPreviews
import com.example.traindriver.ui.util.DateAndTimeFormat.DEFAULT_TIME_TEXT
import com.example.traindriver.ui.util.DateAndTimeFormat.TIME_FORMAT
import com.example.traindriver.ui.util.EmptyDataText.DEFAULT_ENERGY
import com.example.traindriver.ui.util.EmptyDataText.RESULT_ENERGY
import com.example.traindriver.ui.util.double_util.plus
import com.example.traindriver.ui.util.double_util.rounding
import com.example.traindriver.ui.util.double_util.str
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun LocoScreen(response: RouteResponse, navController: NavController) {
    Crossfade(
        targetState = response,
        animationSpec = tween(durationMillis = DURATION_CROSSFADE)
    ) { state ->
        when (state) {
            is ResultState.Loading -> {
                LoadingScreen()
            }
            is ResultState.Success -> state.data?.let { route ->
                DataScreen(route, navController)
            }
            is ResultState.Failure -> {
                FailureScreen()
            }
        }
    }
}

@Composable
private fun LoadingScreen() {
    LoadingElement()
}

@Composable
private fun DataScreen(route: Route, navController: NavController) {
    val scrollState = rememberLazyListState()
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (shadow, data) = createRefs()

        AnimatedVisibility(
            modifier = Modifier.zIndex(1f),
            visible = !scrollState.isScrollInInitialState(),
            enter = fadeIn(animationSpec = tween(durationMillis = 300)),
            exit = fadeOut(animationSpec = tween(durationMillis = 300))
        ) {
            BottomShadow(
                modifier = Modifier
                    .constrainAs(shadow) {
                        top.linkTo(parent.top)
                    },
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .constrainAs(data) {
                    top.linkTo(parent.top)
                },
            state = scrollState
        ) {
            itemsIndexed(route.locoList) { index, item ->
                ItemLocomotive(item, navController)
                if (index == route.locoList.lastIndex) {
                    Spacer(modifier = Modifier.height(60.dp))
                }
            }
        }
    }
}

@Composable
private fun FailureScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = stringResource(id = R.string.route_opening_error), style = Typography.displaySmall)
    }
}

@Composable
fun ItemLocomotive(loco: Locomotive, navController: NavController) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        val (seriesAndNumber, time, sections) = createRefs()

        Box(modifier = Modifier
            .padding(start = 16.dp)
            .constrainAs(seriesAndNumber) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
            }) {
            Row(horizontalArrangement = Arrangement.SpaceAround) {
                val seriesText = loco.series ?: "XXXX"
                val numberText = loco.number ?: "000"
                Text(
                    text = seriesText,
                    color = setTextColor(loco.series),
                    style = Typography.titleMedium
                )
                Text(
                    text = " - ",
                    color = setTextColor(loco.number),
                    style = Typography.titleMedium
                )
                Text(
                    text = numberText,
                    color = setTextColor(loco.number),
                    style = Typography.titleMedium
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
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
                    color = MaterialTheme.colorScheme.secondary,
                    shape = ShapeBackground.small
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    val timeStartAcceptance = loco.timeStartOfAcceptance?.let { millis ->
                        SimpleDateFormat(TIME_FORMAT, Locale.getDefault()).format(millis)
                    } ?: DEFAULT_TIME_TEXT

                    val timeEndAcceptance = loco.timeEndOfAcceptance?.let { millis ->
                        SimpleDateFormat(TIME_FORMAT, Locale.getDefault()).format(millis)
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
                    color = MaterialTheme.colorScheme.secondary,
                    shape = ShapeBackground.small
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    val timeStartDelivery = loco.timeStartOfDelivery?.let { millis ->
                        SimpleDateFormat(TIME_FORMAT, Locale.getDefault()).format(millis)
                    } ?: DEFAULT_TIME_TEXT

                    val timeEndDelivery = loco.timeEndOfDelivery?.let { millis ->
                        SimpleDateFormat(TIME_FORMAT, Locale.getDefault()).format(millis)
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
            .padding(top = 16.dp)) {
            loco.sectionList.forEachIndexed { index, item ->
                if (index.rem(2) != 0) {
                    ItemSection(
                        section = item,
                        navController = navController,
                    )
                } else {
                    ItemSection(
                        section = item,
                        navController = navController,
                    )
                }
            }
            GeneralResult(
                modifier = Modifier.padding(top = 8.dp, end = 16.dp, start = 16.dp),
                loco = loco
            )
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
                        style = Typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
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
                        style = Typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
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
                    .padding(top = 8.dp)
                    .border(
                        width = 0.5.dp,
                        color = MaterialTheme.colorScheme.secondary,
                        shape = ShapeBackground.small
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
                                Text(
                                    text = value.str(),
                                    style = Typography.bodyLarge.copy(color = setTextColor(value))
                                )
                            }
                            section.deliveryRecovery?.let { value ->
                                Text(
                                    text = " - ",
                                    style = Typography.bodyLarge.copy(color = setTextColor(value))
                                )
                                Text(
                                    text = value.str(),
                                    style = Typography.bodyLarge.copy(color = setTextColor(value))
                                )
                            }
                        }
                        section.getRecoveryResult()?.let { value ->
                            Text(
                                text = value.str(),
                                style = Typography.bodyLarge.copy(color = setTextColor(value))
                            )
                        }
                    }
                }
            }
        }
        is SectionDiesel -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .border(
                        width = 0.5.dp,
                        color = MaterialTheme.colorScheme.secondary,
                        shape = ShapeBackground.small
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
                                style = Typography.bodyLarge
                            )
                            Text(
                                text = " - ",
                                color = setTextColor(section.deliveryEnergy),
                                style = Typography.bodyLarge
                            )
                            Text(
                                text = deliveryText,
                                color = setTextColor(section.deliveryEnergy),
                                style = Typography.bodyLarge
                            )
                        }
                        val resultText = "${section.getConsumption()?.str() ?: RESULT_ENERGY} л"
                        Text(
                            text = resultText,
                            color = setTextColor(section.getConsumption()),
                            style = Typography.bodyLarge
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
                                        style = Typography.bodyLarge
                                    )
                                    Text(
                                        text = " - ",
                                        color = setTextColor(section.deliveryInKilo),
                                        style = Typography.bodyLarge
                                    )
                                    Text(
                                        text = deliveryText,
                                        color = setTextColor(section.deliveryInKilo),
                                        style = Typography.bodyLarge
                                    )
                                }
                                val r = section.getConsumptionInKilo()?.let { rounding(it, 2) }
                                val resultText = "${r?.str() ?: RESULT_ENERGY} кг"

                                Text(
                                    text = resultText,
                                    color = setTextColor(section.getConsumptionInKilo()),
                                    style = Typography.bodyLarge
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
                                    style = Typography.bodyMedium
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
                                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.secondary)
                            )
                            Text(
                                text = "${fuel.str()}л",
                                style = Typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
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
    MaterialTheme.colorScheme.onPrimary
} else {
    MaterialTheme.colorScheme.primary
}

@Composable
@DarkLightPreviews
private fun ItemSectionPrev() {
    TrainDriverTheme {
        ItemLocomotive(
            loco = Locomotive(
                series = "2тэ116у",
                number = "338",
                type = false,
                sectionList = listOf(
                    SectionDiesel(
                        acceptedEnergy = 2000.0,
                        deliveryEnergy = 3100.0,
                        coefficient = 0.83,
                        fuelSupply = 2000.0,
                        coefficientSupply = 0.85
                    ),
                    SectionDiesel(
                        acceptedEnergy = 3000.0,
                        deliveryEnergy = 4000.0,
                        coefficient = 0.83,
                        fuelSupply = 2000.0,
                        coefficientSupply = 0.85
                    ),
                )
            ),
            navController = rememberNavController(),
        )
    }
}
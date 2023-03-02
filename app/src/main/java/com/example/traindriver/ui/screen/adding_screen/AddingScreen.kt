package com.example.traindriver.ui.screen.adding_screen

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import com.example.traindriver.R
import com.example.traindriver.ui.element_screen.HorizontalDividerTrainDriver
import com.example.traindriver.ui.screen.Screen
import com.example.traindriver.ui.screen.viewing_route_screen.element.setTextColor
import com.example.traindriver.ui.theme.ColorClickableText
import com.example.traindriver.ui.theme.ShapeBackground
import com.example.traindriver.ui.theme.Typography
import com.example.traindriver.ui.util.DateAndTimeFormat
import com.example.traindriver.ui.util.DateAndTimeFormat.DEFAULT_DATE_TEXT
import com.example.traindriver.ui.util.Tags.LINK_TO_HOME
import java.text.SimpleDateFormat

@Composable
fun AddingScreen(navController: NavController) {
    val dateStart by remember { mutableStateOf<Long?>(null) }
    val dateEnd by remember { mutableStateOf<Long?>(null) }
    var number by remember { mutableStateOf(TextFieldValue("")) }

    Scaffold(
        backgroundColor = MaterialTheme.colors.background,
        topBar = { AddingAppBar(navController = navController) }
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            val (times, list, resultTime) = createRefs()
            Text(
                modifier = Modifier
                    .padding(24.dp)
                    .constrainAs(resultTime) {
                        top.linkTo(parent.top)
                        bottom.linkTo(times.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                text = "11:30",
                style = Typography.h1.copy(color = MaterialTheme.colors.primary)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
                    .constrainAs(times) {
                        top.linkTo(resultTime.bottom)
                        bottom.linkTo(list.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(
                    modifier = Modifier
                        .border(
                            width = 0.5.dp,
                            shape = ShapeBackground.medium,
                            color = MaterialTheme.colors.secondary
                        )
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val dateStartText = dateStart?.let { millis ->
                        SimpleDateFormat(DateAndTimeFormat.DATE_FORMAT).format(millis)
                    } ?: DEFAULT_DATE_TEXT

                    Text(
                        text = dateStartText,
                        style = Typography.body1,
                        color = setTextColor(dateStart)
                    )
                    dateStart?.let { millis ->
                        val time = SimpleDateFormat(DateAndTimeFormat.TIME_FORMAT).format(millis)
                        Text(
                            text = time,
                            style = Typography.body1,
                            color = setTextColor(dateStart)
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .border(
                            width = 0.5.dp,
                            shape = ShapeBackground.medium,
                            color = MaterialTheme.colors.secondary
                        )
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val dateStartText = dateEnd?.let { millis ->
                        SimpleDateFormat(DateAndTimeFormat.DATE_FORMAT).format(millis)
                    } ?: DEFAULT_DATE_TEXT

                    Text(
                        text = dateStartText,
                        style = Typography.body1,
                        color = setTextColor(dateEnd)
                    )
                    dateEnd?.let { millis ->
                        val time = SimpleDateFormat(DateAndTimeFormat.TIME_FORMAT).format(millis)
                        Text(
                            text = time,
                            style = Typography.body1,
                            color = setTextColor(dateEnd)
                        )
                    }
                }
            }
            Column(modifier = Modifier
                .constrainAs(list) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                    width = Dimension.fillToConstraints
                }
                .padding(vertical = 32.dp)) {
                NumberEditItem(
                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 24.dp),
                    value = number,
                    onValueChange = { number = it }
                )
                HorizontalDividerTrainDriver(modifier = Modifier.padding(horizontal = 24.dp))
                Spacer(modifier = Modifier.height(24.dp))
                ItemAddLoco()
                HorizontalDividerTrainDriver(modifier = Modifier.padding(horizontal = 24.dp))
                ItemAddLoco()
                HorizontalDividerTrainDriver(modifier = Modifier.padding(horizontal = 24.dp))
                ItemAddLoco()
            }
        }
    }
}

@Composable
fun NumberEditItem(
    modifier: Modifier = Modifier,
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit
) {
    BasicTextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        textStyle = Typography.subtitle2.copy(color = MaterialTheme.colors.primary),
        decorationBox = { innerTextField ->
            Box(
                contentAlignment = Alignment.CenterStart
            ) {
                if (value.text.isEmpty()) {
                    Text(
                        text = "Номер маршрута",
                        style = Typography.subtitle2,
                        color = MaterialTheme.colors.primaryVariant
                    )
                }
                innerTextField()
            }
        }
    )
}

@Composable
fun ItemAddLoco() {
    Card(
        backgroundColor = Color.Transparent,
        elevation = 0.dp
    ) {
        ConstraintLayout(
            modifier = Modifier
                .clickable { }
                .padding(vertical = 16.dp, horizontal = 24.dp)
                .fillMaxWidth()
        ) {
            val (title, data, icon) = createRefs()
            createVerticalChain(title, data, chainStyle = ChainStyle.SpreadInside)
            Text(
                modifier = Modifier
                    .constrainAs(title) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        bottom.linkTo(data.top)
                    }
                    .padding(bottom = 8.dp),
                text = "Локомотив",
                style = Typography.subtitle2.copy(color = MaterialTheme.colors.primary)
            )
            Text(
                modifier = Modifier
                    .constrainAs(data) {
                        start.linkTo(parent.start)
                        bottom.linkTo(parent.bottom)
                    },
                text = "2эс4к №001",
                style = Typography.body2.copy(color = MaterialTheme.colors.primaryVariant)
            )
            Image(
                modifier = Modifier
                    .size(20.dp)
                    .constrainAs(icon) {
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                        top.linkTo(parent.top)
                    },
                painter = painterResource(id = R.drawable.ic_forward_24),
                contentDescription = null,
                colorFilter = ColorFilter.tint(MaterialTheme.colors.secondary)
            )
        }
    }
}

@Composable
private fun AddingAppBar(navController: NavController) {
    TopAppBar(
        modifier = Modifier
            .fillMaxHeight(0.12f),
        backgroundColor = MaterialTheme.colors.background
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 12.dp, end = 16.dp, bottom = 6.dp)
        ) {
            val (icon, button) = createRefs()
            IconButton(
                modifier = Modifier
                    .size(dimensionResource(id = R.dimen.min_size_view))
                    .constrainAs(icon) {
                        start.linkTo(parent.start)
                        bottom.linkTo(parent.bottom)
                    },
                onClick = {
                    navController.navigateUp()
                }
            ) {
                Image(
                    modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size)),
                    painter = painterResource(id = R.drawable.ic_back_24),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(MaterialTheme.colors.primary)
                )
            }

            val linkText = buildAnnotatedString {
                val text = "Сохранить"
                val start = 0
                val end = text.length
                append(text)

                addStyle(
                    style = SpanStyle(
                        color = ColorClickableText,
                    ), start, end
                )

                addStringAnnotation(
                    tag = LINK_TO_HOME,
                    annotation = Screen.Home.route,
                    start = start,
                    end = end
                )
            }

            ClickableText(
                modifier = Modifier.constrainAs(button) {
                    end.linkTo(parent.end)
                    top.linkTo(icon.top)
                    bottom.linkTo(icon.bottom)
                },
                text = linkText,
                style = Typography.button.copy(color = ColorClickableText)
            ) {
                // TODO SAVE TO REPOSITORY
                linkText.getStringAnnotations(LINK_TO_HOME, it, it)
                    .firstOrNull()?.let { stringAnnotation ->
                        navController.navigate(stringAnnotation.item)
                    }
            }
        }
    }
}
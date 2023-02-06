package com.example.traindriver.ui.screen.main_screen.elements

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension.Companion.fillToConstraints
import com.example.traindriver.ui.element_screen.HorizontalDividerTrainDriver
import com.example.traindriver.ui.element_screen.VerticalDividerTrainDriver
import com.example.traindriver.ui.theme.ShapeBackground
import com.example.traindriver.ui.theme.TrainDriverTheme
import com.example.traindriver.ui.util.DarkLightPreviews
import com.example.traindriver.ui.util.FontScalePreviews

@Composable
fun ItemMainScreen() {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, start = 8.dp, end = 8.dp)
            .height(80.dp)
            .border(
                width = 1.dp, color = MaterialTheme.colors.primary, shape = ShapeBackground.medium
            )
    ) {
        val (date, verticalDividerFirst, station, verticalDividerSecond, workTime) = createRefs()
        DateElementItem(modifier = Modifier
            .padding(horizontal = 16.dp)
            .constrainAs(date) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
            })
        VerticalDividerTrainDriver(modifier = Modifier
            .constrainAs(verticalDividerFirst) {
                start.linkTo(date.end)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            }
            .padding(vertical = 10.dp))
        StationElementItem(modifier = Modifier
            .constrainAs(station) {
                top.linkTo(parent.top)
                start.linkTo(verticalDividerFirst.end)
                end.linkTo(verticalDividerSecond.start)
                bottom.linkTo(parent.bottom)
                width = fillToConstraints
                height = fillToConstraints
            }
            .padding(horizontal = 16.dp))
        VerticalDividerTrainDriver(modifier = Modifier
            .constrainAs(verticalDividerSecond) {
                end.linkTo(workTime.start)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            }
            .padding(vertical = 10.dp))
        WorkTimeElementItem(modifier = Modifier
            .constrainAs(workTime) {
                end.linkTo(parent.end)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            }
            .padding(horizontal = 16.dp))
    }
}

@Composable
fun WorkTimeElementItem(
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        Text(text = "10")
        Text(text = ":")
        Text(text = "30")
    }
}

@Composable
fun StationElementItem(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.Start
    ) {
        Row {
            Text(text = "Лужская", maxLines = 1)
            Text(text = " - ", maxLines = 1)
            Text(text = "Луга", maxLines = 1)
        }
        Row {
            Text(text = "3эс4к №064", maxLines = 1)
            Text(text = "   ", maxLines = 1)
            Text(text = "№ 2220", overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
fun DateElementItem(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .wrapContentWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(text = "02", fontSize = MaterialTheme.typography.h3.fontSize)
        HorizontalDividerTrainDriver(modifier = Modifier.width(30.dp))
        Text(text = "01", fontSize = MaterialTheme.typography.h3.fontSize)
    }
}

@Composable
@DarkLightPreviews
@FontScalePreviews
private fun PreviewItem() {
    TrainDriverTheme {
        ItemMainScreen()
    }
}
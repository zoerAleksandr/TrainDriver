package com.example.traindriver.ui.screen.adding_screen.custom_tab

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.traindriver.ui.theme.ShapeBackground
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import kotlinx.coroutines.launch

@Composable
private fun MyTabIndicator(
    indicatorWidth: Dp,
    indicatorOffset: Dp,
    indicatorColor: Color,
) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(
                width = indicatorWidth,
            )
            .offset(
                x = indicatorOffset,
            )
            .clip(
                shape = ShapeBackground.small,
            )
            .background(
                color = indicatorColor,
            )
    )
}

@Composable
private fun MyTabItem(
    isSelected: Boolean,
    onClick: () -> Unit,
    tabWidth: Dp,
    text: String,
) {
    val tabTextColor: Color by animateColorAsState(
        targetValue = if (isSelected) {
            MaterialTheme.colors.onPrimary
        } else {
            MaterialTheme.colors.primaryVariant
        },
        animationSpec = tween(durationMillis = 100),
    )
    Text(
        modifier = Modifier
            .clip(ShapeBackground.small)
            .clickable {
                onClick()
            }
            .width(tabWidth)
            .padding(
                vertical = 8.dp,
                horizontal = 12.dp,
            ),
        text = text,
        color = tabTextColor,
        textAlign = TextAlign.Center
    )
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun CustomTab(
    selectedItemIndex: Int,
    items: List<String>,
    modifier: Modifier = Modifier,
    tabWidth: Dp,
    pagerState: PagerState,
) {
    val indicatorOffset: Dp by animateDpAsState(
        targetValue = tabWidth * selectedItemIndex,
        animationSpec = tween(durationMillis = 300, easing = LinearEasing),
    )

    Box(
        modifier = modifier
            .clip(ShapeBackground.small)
            .border(
                width = 1.dp,
                color = MaterialTheme.colors.secondary,
                shape = ShapeBackground.small
            )
            .background(MaterialTheme.colors.background)
            .height(intrinsicSize = IntrinsicSize.Min),
    ) {
        MyTabIndicator(
            indicatorWidth = tabWidth,
            indicatorOffset = indicatorOffset,
            indicatorColor = MaterialTheme.colors.primary,
        )
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.clip(ShapeBackground.small),
        ) {
            val scope = rememberCoroutineScope()
            items.mapIndexed { index, text ->
                val isSelected = index == selectedItemIndex
                MyTabItem(
                    isSelected = isSelected,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(
                                page = index, animationSpec = tween(durationMillis = 300)
                            )
                        }
                    },
                    tabWidth = tabWidth,
                    text = text,
                )
            }
        }
    }
}
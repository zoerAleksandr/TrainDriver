package com.example.traindriver.ui.screen.main_screen.elements

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.traindriver.R

@Composable
fun TopBarMainScreen() {
    Row(
        modifier = Modifier
            .fillMaxHeight(0.12f)
            .fillMaxWidth()
            .padding(dimensionResource(id = R.dimen.medium_padding)),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        IconButton(modifier = Modifier
            .size(dimensionResource(id = R.dimen.min_size_view))
            .background(
                color = MaterialTheme.colors.surface, shape = CircleShape
            ), onClick = { /*TODO*/ }) {
            Image(
                modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size)),
                painter = painterResource(id = R.drawable.person_icon),
                contentDescription = stringResource(id = R.string.account)
            )
        }
        MonthButton(modifier = Modifier.clickable {
            // TODO
        })
        IconButton(modifier = Modifier
            .size(dimensionResource(id = R.dimen.min_size_view))
            .background(
                color = MaterialTheme.colors.surface, shape = CircleShape
            ), onClick = { /*TODO*/ }) {
            Image(
                modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size)),
                painter = painterResource(id = R.drawable.search_icon),
                contentDescription = stringResource(id = R.string.search)
            )
        }
    }
}
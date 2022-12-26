package com.example.traindriver.ui.screen.signin_screen.elements

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.traindriver.ui.theme.TrainDriverTheme
import com.example.traindriver.ui.util.DarkLightPreviews
import com.example.traindriver.ui.util.FontScalePreviews
import com.example.traindriver.ui.util.LocaleUser

@Composable
fun BottomSheetLocaleListItem(locale: LocaleUser, onItemClick: (LocaleUser) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(bottom = 8.dp)
            .clickable(onClick = { onItemClick(locale) }),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier
                .fillMaxHeight(),
            painter = painterResource(locale.icon),
            contentDescription = locale.contentDescription
        )
        Text(
            modifier = Modifier
                .padding(start = 16.dp),
            text = locale.contentDescription,
            color = MaterialTheme.colors.primary,
        )
    }
}

@Composable
@DarkLightPreviews
@FontScalePreviews
fun DefaultPreview() {
    TrainDriverTheme {
        BottomSheetLocaleListItem(LocaleUser.RU, {})
    }
}
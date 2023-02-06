package com.example.traindriver.ui.screen.main_screen.elements

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.traindriver.ui.element_screen.HandleBottomSheet

@Composable
fun BottomSheetContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.88f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HandleBottomSheet()
        LazyColumn(modifier = Modifier.padding(top = 32.dp)) {
            items(20) {
                ItemMainScreen()
            }
            //TODO items
        }
    }
}
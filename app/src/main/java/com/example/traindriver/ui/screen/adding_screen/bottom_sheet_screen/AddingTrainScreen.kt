package com.example.traindriver.ui.screen.adding_screen.bottom_sheet_screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.traindriver.ui.screen.adding_screen.AddingViewModel

@Composable
fun AddingTrainScreen(
    navController: NavController,
    trainId: String? = null,
    addingRouteViewModel: AddingViewModel,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "AddingTrainScreen")
    }
}
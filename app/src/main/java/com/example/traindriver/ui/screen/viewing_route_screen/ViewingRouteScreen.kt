package com.example.traindriver.ui.screen.viewing_route_screen

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.traindriver.ui.util.Constants.ROUTE

@Composable
fun ViewingRouteScreen(navController: NavController) {
    val args = navController.currentBackStackEntry?.arguments?.getString(ROUTE)
    Scaffold {
        Text(modifier = Modifier.padding(top = 16.dp), text = args.toString())
    }
}
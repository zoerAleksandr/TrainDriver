package com.example.traindriver.ui.screen.viewing_route_screen

import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.traindriver.ui.util.Constants.ROUTE

@Composable
fun ViewingRouteScreen(navController: NavController) {
    val args = navController.currentBackStackEntry?.arguments?.getString(ROUTE)
    Scaffold {
        Text(text = args.toString())
    }
}
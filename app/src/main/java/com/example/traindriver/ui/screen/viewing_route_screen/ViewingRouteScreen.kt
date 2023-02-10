package com.example.traindriver.ui.screen.viewing_route_screen

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.traindriver.data.util.ResultState
import com.example.traindriver.ui.util.Constants.ROUTE
import com.example.traindriver.ui.util.OnLifecycleEvent

@Composable
fun ViewingRouteScreen(
    navController: NavController,
    viewModel: ViewingRouteViewModel = viewModel()
) {
    val uid = navController.currentBackStackEntry?.arguments?.getString(ROUTE)
    OnLifecycleEvent { _, event ->
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                uid?.let {
                    viewModel.getRouteById(uid)
                }
            }
            else -> {}
        }
    }

    val state = viewModel.routeState
    var numberRoute by mutableStateOf("")

    Scaffold {
        when (state) {
            is ResultState.Loading -> {
                Log.d("ZZZ", "Loading")
            }
            is ResultState.Success -> state.data?.let { route ->
                Log.d("ZZZ", "!!!! $route")
                numberRoute = route.id
            }
            is ResultState.Failure -> {}
        }
        Text(modifier = Modifier.padding(top = 16.dp), text = numberRoute)
    }
}
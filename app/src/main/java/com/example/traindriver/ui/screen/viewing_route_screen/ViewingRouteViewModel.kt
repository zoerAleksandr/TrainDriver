package com.example.traindriver.ui.screen.viewing_route_screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.traindriver.data.util.ResultState
import com.example.traindriver.domain.entity.Route
import com.example.traindriver.domain.use_case.GetRouteByIdUseCase
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

typealias RouteResponse = ResultState<Route>

class ViewingRouteViewModel : ViewModel(), KoinComponent {
    private val getRouteByIdUseCase: GetRouteByIdUseCase by inject()

    var routeState by mutableStateOf<RouteResponse>(ResultState.Success(null))
        private set

    fun getRouteById(id: String) {
        viewModelScope.launch {
            getRouteByIdUseCase.execute(id).collect { response ->
                routeState = response
            }
        }
    }
}
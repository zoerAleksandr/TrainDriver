package com.example.traindriver.ui.screen.main_screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.traindriver.data.util.ResultState
import com.example.traindriver.domain.entity.Route
import com.example.traindriver.domain.use_case.GetRouteListByMonthUseCase
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

typealias RouteListByMonthResponse = ResultState<List<Route>>

class MainViewModel : ViewModel(), KoinComponent {
    private val getRouteListByMonthUseCase: GetRouteListByMonthUseCase by inject()

    var listRoute by mutableStateOf<RouteListByMonthResponse>(ResultState.Loading())
        private set

    var totalTime by mutableStateOf(0L)
        private set

    fun getListItineraryByMonth(month: Int) {
        viewModelScope.launch {
            getRouteListByMonthUseCase.execute(month).collect { result ->
                listRoute = result
                if (result is ResultState.Success) {
                    result.data?.let { list ->
                        calculationOfTotalTime(list)
                    }
                }
            }
        }
    }

    private fun calculationOfTotalTime(listRoute: List<Route>) {
        totalTime = 0
        for (item in listRoute) {
            item.getWorkTime().let { routeTime ->
                totalTime += routeTime ?: 0
            }
        }
    }
}
package com.example.traindriver.domain.use_case

import com.example.traindriver.domain.repository.DataRepository
import com.example.traindriver.ui.screen.main_screen.RouteListByMonthResponse
import kotlinx.coroutines.flow.Flow

class GetRouteListByMonthUseCase(val repository: DataRepository) {
    fun execute(month: Int): Flow<RouteListByMonthResponse> =
        repository.getListItineraryByMonth(month)
}
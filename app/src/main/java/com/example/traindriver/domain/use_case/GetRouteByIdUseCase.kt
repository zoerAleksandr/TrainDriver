package com.example.traindriver.domain.use_case

import com.example.traindriver.data.repository.DataRepository
import com.example.traindriver.ui.screen.main_screen.RouteResponse
import kotlinx.coroutines.flow.Flow

class GetRouteByIdUseCase(val repository: DataRepository) {
    fun execute(uid: String): Flow<RouteResponse> =
        repository.getItineraryById(uid)
}
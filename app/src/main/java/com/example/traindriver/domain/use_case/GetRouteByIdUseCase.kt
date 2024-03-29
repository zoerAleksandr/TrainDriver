package com.example.traindriver.domain.use_case

import com.example.traindriver.domain.repository.DataRepository
import com.example.traindriver.ui.screen.viewing_route_screen.RouteResponse
import kotlinx.coroutines.flow.Flow

class GetRouteByIdUseCase(val repository: DataRepository) {
    fun execute(uid: String): Flow<RouteResponse> =
        repository.getItineraryById(uid)
}
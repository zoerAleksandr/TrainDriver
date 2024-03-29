package com.example.traindriver.domain.repository

import com.example.traindriver.data.util.ResultState
import com.example.traindriver.domain.entity.Route
import com.example.traindriver.ui.screen.main_screen.RouteListByMonthResponse
import com.example.traindriver.ui.screen.viewing_route_screen.RouteResponse
import kotlinx.coroutines.flow.Flow

interface DataRepository {
    fun getListItineraryByMonth(month: Int): Flow<RouteListByMonthResponse>
    fun getItineraryById(id: String): Flow<RouteResponse>
    fun addRoute(route: Route): Flow<ResultState<Boolean>>
}
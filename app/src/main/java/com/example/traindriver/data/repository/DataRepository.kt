package com.example.traindriver.data.repository

import com.example.traindriver.ui.screen.main_screen.RouteListByMonthResponse
import com.example.traindriver.ui.screen.main_screen.RouteResponse
import kotlinx.coroutines.flow.Flow

interface DataRepository {
    fun getListItineraryByMonth(month: Int): Flow<RouteListByMonthResponse>
    fun getItineraryById(id: String): Flow<RouteResponse>
}
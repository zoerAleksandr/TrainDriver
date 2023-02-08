package com.example.traindriver.data.repository.mock

import com.example.traindriver.data.repository.DataRepository
import com.example.traindriver.data.util.ResultState
import com.example.traindriver.domain.entity.Itinerary
import com.example.traindriver.domain.entity.Locomotive
import com.example.traindriver.domain.entity.Station
import com.example.traindriver.domain.entity.Train
import com.example.traindriver.ui.screen.main_screen.RouteListByMonthResponse
import com.example.traindriver.ui.screen.main_screen.RouteResponse
import com.example.traindriver.ui.util.currentTimeInLong
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class MockDataRepository : DataRepository {
    private val one = Itinerary(
        timeEndWork = currentTimeInLong() + 10000000,
        stationList = mutableListOf(
            Station(stationName = "Луга"),
            Station(stationName = "СПБСМ")
        ),
        trainList = mutableListOf(
            Train(number = "2220")
        ),
        locoList = mutableListOf(
            Locomotive(series = "2эс4к", number = "104")
        )
    )
    private val two = Itinerary(
        timeEndWork = currentTimeInLong() + 10000000,
        stationList = mutableListOf(
            Station(stationName = "Лужская"),
            Station(stationName = "Луга")
        ),
        trainList = mutableListOf(
            Train(number = "1954")
        ),
        locoList = mutableListOf(
            Locomotive(series = "ВЛ10", number = "598")
        )
    )
    private val three = Itinerary(
        timeEndWork = currentTimeInLong() + 10000000,
        stationList = mutableListOf(
            Station(stationName = "Лужская"),
            Station(stationName = "Будогощь")
        ),
        trainList = mutableListOf(
            Train(number = "2350")
        ),
        locoList = mutableListOf(
            Locomotive(series = "2эс4к", number = "164"),
            Locomotive(series = "3эс4к", number = "064"),
        )
    )

    override fun getListItineraryByMonth(month: Int): Flow<RouteListByMonthResponse> =
        callbackFlow {
            trySend(ResultState.Loading())
            delay(4000)
            val list = listOf(one, two, three)
            trySend(ResultState.Success(listOf()))
            awaitClose { close() }
        }

    override fun getItineraryById(id: String): Flow<RouteResponse> =
        callbackFlow {
            trySend(ResultState.Loading())
            delay(4000)
            trySend(ResultState.Success(one))
            awaitClose { close() }
        }
}
package com.example.traindriver.data.repository.mock

import com.example.traindriver.data.util.ResultState
import com.example.traindriver.domain.entity.*
import com.example.traindriver.domain.repository.DataRepository
import com.example.traindriver.ui.screen.main_screen.RouteListByMonthResponse
import com.example.traindriver.ui.screen.viewing_route_screen.RouteResponse
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class MockDataRepository : DataRepository {
    private val one = Route(
        number = 139,
        timeStartWork = 1_675_789_800_000,
        timeEndWork = 1_675_876_200_000,
        stationList = mutableListOf(
            Station(stationName = "Луга"),
            Station(stationName = "СПБСМ")
        ),
        trainList = mutableListOf(
            Train(number = "2220")
        ),
        locoList = mutableListOf(
            Locomotive(
                series = "2тэ116у",
                number = "338",
                type = false,
                sectionList = listOf(
                    SectionDiesel(
                        acceptedEnergy = 2000.0,
                        deliveryEnergy = 3100.0,
                        coefficient = 0.83,
                        fuelSupply = 2000.0,
                        coefficientSupply = 0.85
                    ),
                    SectionDiesel(
                        acceptedEnergy = 3000.0,
                        deliveryEnergy = 4000.0,
//                        coefficient = 0.83,
                        fuelSupply = 2000.0,
//                        coefficientSupply = 0.85
                    ),
                )
            ),
            Locomotive(
                series = "2эс4к",
                number = "104",
                type = true,
                sectionList = listOf(
                    SectionElectric(
                        acceptedEnergy = 122009.0,
                        deliveryEnergy = 122033.0,
//                        acceptedRecovery = 9043.0,
//                        deliveryRecovery = 9049.0
                    ),
                    SectionElectric(
                        acceptedEnergy = 122009.0,
                        deliveryEnergy = 122034.0,
//                        acceptedRecovery = 9043.0,
//                        deliveryRecovery = 9049.0
                    ),
                )
            ),
            Locomotive(
                series = "2эс4к",
                number = "104",
                type = true,
                sectionList = listOf(
                    SectionElectric(
                        acceptedEnergy = 122009.0,
                        deliveryEnergy = 122033.0,
                        acceptedRecovery = 9043.0,
                        deliveryRecovery = 9049.0
                    ),
                    SectionElectric(
                        acceptedEnergy = 122009.0,
                        deliveryEnergy = 122034.0,
//                        acceptedRecovery = 9043.0,
//                        deliveryRecovery = 9049.0
                    ),
                )
            ),

        )
    )
    private val two = Route(
        number = 1989,
        timeStartWork = 1_675_857_000_000,
        timeEndWork = 1_675_876_200_000,
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

    private
    val three = Route(
        number = 4490,
        timeStartWork = 1_675_857_000_000,
        timeEndWork = 1_675_876_200_000,
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
            delay(2000)
            val list = listOf(one, two, three)
            trySend(ResultState.Success(list))
            awaitClose { close() }
        }

    override fun getItineraryById(id: String): Flow<RouteResponse> =
        callbackFlow {
            trySend(ResultState.Loading())
            delay(2000)
            trySend(ResultState.Success(one))
            awaitClose { close() }
        }
}
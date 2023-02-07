package com.example.traindriver.domain.entity

import com.example.traindriver.ui.util.currentTimeInLong
import java.util.*

fun generateUid() = UUID.randomUUID().toString()

data class Itinerary(
    val id: String = generateUid(),

    var timeStartWork: Long? = currentTimeInLong(),
    val timeEndWork: Long? = null,

    val locoList: MutableList<Locomotive> = mutableListOf(),
    val trainList: MutableList<Train> = mutableListOf(),
    val stationList: MutableList<Station> = mutableListOf(),
    val passengerList: MutableList<Passenger> = mutableListOf()
) {
    fun getWorkTime(): Long? {
        val timeEnd = timeEndWork
        val timeStart = timeStartWork
        return if (timeEnd != null && timeStart != null) {
            timeEnd - timeStart
        } else {
            null
        }
    }
}

data class Train(
    val id: String = generateUid(),
    var number: String? = null,
    var weight: Int? = null,
    var axle: Int? = null,
    var conditionalLength: Int? = null
)

data class Passenger(
    val id: String = generateUid(),
    val stationDeparture: String? = null,
    val stationArrival: String? = null,
    val timeArrival: Long? = currentTimeInLong(),
    val timeDeparture: Long? = null
)

data class Station(
    val id: String = generateUid(),
    var stationName: String? = null,
    var timeArrival: Long? = null,
    var timeDeparture: Long? = null
)


/**
 * type locomotive when true = electric locomotive, when false = diesel locomotive
 * */
data class Locomotive(
    val id: String = generateUid(),
    var series: String? = null,
    var number: String? = null,
    var type: Boolean = true,
    var sectionList: List<Section> = listOf(),

    var timeStartOfAcceptance: Long? = currentTimeInLong(),
    var timeEndOfAcceptance: Long? = null,
    var timeStartOfDelivery: Long? = null,
    var timeEndOfDelivery: Long? = null
)

data class Section(
    val id: String = generateUid(),
    var acceptedEnergy: Double? = null,
    var deliveryEnergy: Double? = null
)

package com.example.traindriver.domain.entity

import com.example.traindriver.ui.screen.viewing_route_screen.element.rounding
import com.example.traindriver.ui.util.currentTimeInLong
import java.math.BigDecimal
import java.util.*

fun generateUid() = UUID.randomUUID().toString()

data class Route(
    val id: String = generateUid(),
    var number: Int? = null,
    var timeStartWork: Long? = null,
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

    var timeStartOfAcceptance: Long? = null,
    var timeEndOfAcceptance: Long? = null,
    var timeStartOfDelivery: Long? = null,
    var timeEndOfDelivery: Long? = null
)

operator fun Double?.minus(other: Double?): Double? =
    if (this != null && other != null) {
        this - other
    } else {
        null
    }

operator fun Double?.times(other: Double?): Double? =
    if (this != null && other != null) {
        this * other
    } else {
        null
    }

fun Double.countCharsAfterDecimalPoint(): Int {
    return BigDecimal.valueOf(this).scale()
}

fun differenceBetweenDouble(value1: Double?, value2: Double?): Double? {
    val countAfterPoint1: Int = value1?.countCharsAfterDecimalPoint() ?: 0
    val countAfterPoint2: Int = value2?.countCharsAfterDecimalPoint() ?: 0
    val maxCount = if (countAfterPoint1 > countAfterPoint2) {
        countAfterPoint1
    } else {
        countAfterPoint2
    }
    val result = value2 - value1
    return result?.let {
        rounding(it, maxCount)
    }
}

fun reverseDifferenceBetweenDouble(value1: Double?, value2: Double?): Double? {
    val countAfterPoint1: Int = value1?.countCharsAfterDecimalPoint() ?: 0
    val countAfterPoint2: Int = value2?.countCharsAfterDecimalPoint() ?: 0
    val maxCount = if (countAfterPoint1 > countAfterPoint2) {
        countAfterPoint1
    } else {
        countAfterPoint2
    }
    val result = value1 - value2
    return result?.let {
        rounding(it, maxCount)
    }
}

data class SectionElectric(
    override val id: String = generateUid(),
    override var acceptedEnergy: Double? = null,
    override var deliveryEnergy: Double? = null,
    var acceptedRecovery: Double? = null,
    var deliveryRecovery: Double? = null
) : Section(id, acceptedEnergy, deliveryEnergy) {

    override fun getConsumption() = differenceBetweenDouble(acceptedEnergy, deliveryEnergy)
    fun getRecoveryResult() = differenceBetweenDouble(acceptedRecovery, deliveryRecovery)
}

data class SectionDiesel(
    override val id: String = generateUid(),
    override val acceptedEnergy: Double? = null,
    override val deliveryEnergy: Double? = null,
    var coefficient: Double? = null,
    var acceptedInKilo: Double? = acceptedEnergy * coefficient,
    var deliveryInKilo: Double? = deliveryEnergy * coefficient
) : Section(id, acceptedEnergy, deliveryEnergy) {
    override fun getConsumption() = reverseDifferenceBetweenDouble(acceptedEnergy, deliveryEnergy)
    fun getConsumptionInKilo() = reverseDifferenceBetweenDouble(acceptedInKilo, deliveryInKilo)
}

abstract class Section(
    open val id: String,
    open val acceptedEnergy: Double?,
    open val deliveryEnergy: Double?
) {
    abstract fun getConsumption(): Double?
}

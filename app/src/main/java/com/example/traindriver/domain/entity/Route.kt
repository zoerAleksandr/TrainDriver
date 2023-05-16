package com.example.traindriver.domain.entity

import com.example.traindriver.ui.util.double_util.times
import java.util.*

fun generateUid() = UUID.randomUUID().toString()

data class Route(
    val id: String = generateUid(),
    var number: String? = null,
    var timeStartWork: Long? = null,
    var timeEndWork: Long? = null,

    val locoList: MutableList<Locomotive> = mutableListOf(),
    val trainList: MutableList<Train> = mutableListOf(),
    val stationList: MutableList<Station> = mutableListOf(),
    val passengerList: MutableList<Passenger> = mutableListOf(),
    var notes: Notes? = null
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

    override fun equals(other: Any?): Boolean {
        if (other is Route){
            return other.id == this.id
        }
        return super.equals(other)
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + (number?.hashCode() ?: 0)
        result = 31 * result + (timeStartWork?.hashCode() ?: 0)
        result = 31 * result + (timeEndWork?.hashCode() ?: 0)
        result = 31 * result + locoList.hashCode()
        result = 31 * result + trainList.hashCode()
        result = 31 * result + stationList.hashCode()
        result = 31 * result + passengerList.hashCode()
        return result
    }
}

data class Train(
    val id: String = generateUid(),
    var number: String? = null,
    var weight: Int? = null,
    var axle: Int? = null,
    var conditionalLength: Int? = null,
    @get:JvmName("locomotiveTrain") var locomotive: Locomotive? = null,
    var stations: MutableList<Station> = mutableListOf()
)

data class Passenger(
    val id: String = generateUid(),
    var trainNumber: String? = null,
    var stationDeparture: String? = null,
    var stationArrival: String? = null,
    var timeArrival: Long? = null,
    var timeDeparture: Long? = null,
    var notes: String? = null
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

data class SectionElectric(
    override val id: String = generateUid(),
    override var acceptedEnergy: Double? = null,
    override var deliveryEnergy: Double? = null,
    var acceptedRecovery: Double? = null,
    var deliveryRecovery: Double? = null
) : Section(id, acceptedEnergy, deliveryEnergy) {

    override fun getConsumption() = Calculation.getTotalEnergyConsumption(acceptedEnergy, deliveryEnergy)
    fun getRecoveryResult() = Calculation.getTotalEnergyConsumption(acceptedRecovery, deliveryRecovery)
}

data class SectionDiesel(
    override val id: String = generateUid(),
    override val acceptedEnergy: Double? = null,
    override val deliveryEnergy: Double? = null,
    var coefficient: Double? = null,
    var acceptedInKilo: Double? = acceptedEnergy * coefficient,
    var deliveryInKilo: Double? = deliveryEnergy * coefficient,
    var fuelSupply: Double? = null,
    var coefficientSupply: Double? = null,
    var fuelSupplyInKilo: Double? = fuelSupply * coefficientSupply
) : Section(id, acceptedEnergy, deliveryEnergy) {
    override fun getConsumption() = Calculation.getTotalFuelConsumption(acceptedEnergy, deliveryEnergy, fuelSupply)

    fun getConsumptionInKilo() = Calculation.getTotalFuelInKiloConsumption(getConsumption(), coefficient)
}

abstract class Section(
    open val id: String,
    open val acceptedEnergy: Double?,
    open val deliveryEnergy: Double?
) {
    abstract fun getConsumption(): Double?
}

data class Notes(
    val id: String = generateUid(),
    var text: String? = null,
    var photos: MutableList<String?> = mutableListOf()
)

package com.example.traindriver.domain.entity

import com.example.traindriver.ui.util.double_util.plusNullableValue
import com.example.traindriver.ui.util.double_util.reverseDifferenceBetweenDouble
import com.example.traindriver.ui.util.double_util.times

object Calculation {
    fun getTotalFuelConsumption(
        accepted: Double?,
        delivery: Double?,
        refuel: Double?
    ) : Double?{
        return reverseDifferenceBetweenDouble(accepted, delivery).plusNullableValue(refuel)
    }

    fun getTotalFuelInKiloConsumption(
        consumption: Double?,
        coefficient: Double?
    ): Double?{
        return consumption * coefficient
    }
}
package com.example.traindriver.ui.screen.adding_screen.state_holder

enum class DeliveredType {
    START, END
}

data class DeliveredTimeState(
    val time: Long? = null,
    val type: DeliveredType
)

data class DeliveryBlockState(
    val startDelivered: DeliveredTimeState = DeliveredTimeState(type = DeliveredType.START),
    val endDelivered: DeliveredTimeState = DeliveredTimeState(type = DeliveredType.END),
    val formValid: Boolean,
    var errorMessage: String = ""
)

sealed class DeliveryEvent {
    data class EnteredStartDelivery(val value: Long?) : DeliveryEvent()
    data class EnteredEndDelivery(val value: Long?) : DeliveryEvent()
    data class FocusChange(val fieldName: DeliveredType) : DeliveryEvent()
}
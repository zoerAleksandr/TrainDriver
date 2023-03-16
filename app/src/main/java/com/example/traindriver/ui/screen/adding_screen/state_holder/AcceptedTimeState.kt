package com.example.traindriver.ui.screen.adding_screen.state_holder

enum class AcceptedType {
    START, END
}

data class AcceptedTimeState(
    val time: Long? = null,
    val type: AcceptedType
)

data class AcceptedBlockState(
    val startAccepted: AcceptedTimeState = AcceptedTimeState(type = AcceptedType.START),
    val endAccepted: AcceptedTimeState = AcceptedTimeState(type = AcceptedType.END),
    val formValid: Boolean,
    var errorMessage: String = ""
)

sealed class AcceptedEvent {
    data class EnteredStartAccepted(val value: Long?) : AcceptedEvent()
    data class EnteredEndAccepted(val value: Long?) : AcceptedEvent()
    data class FocusChange(val fieldName: AcceptedType) : AcceptedEvent()
}

package com.example.traindriver.ui.screen.adding_screen.state_holder

enum class ElectricSectionType {
    ACCEPTED, DELIVERY, RECOVERY_ACCEPTED, RECOVERY_DELIVERY
}

data class ElectricSectionFieldState(
    val data: String? = null,
    val type: ElectricSectionType
)

sealed class ElectricSectionEvent {
    data class EnteredAccepted(val index: Int, val data: String?) : ElectricSectionEvent()
    data class EnteredDelivery(val index: Int, val data: String?) : ElectricSectionEvent()
    data class EnteredRecoveryAccepted(val index: Int, val data: String?) : ElectricSectionEvent()
    data class EnteredRecoveryDelivery(val index: Int, val data: String?) : ElectricSectionEvent()
    data class FocusChange(val index: Int, val fieldName: ElectricSectionType) :
        ElectricSectionEvent()
}
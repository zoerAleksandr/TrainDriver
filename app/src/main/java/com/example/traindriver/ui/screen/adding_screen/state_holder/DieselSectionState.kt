package com.example.traindriver.ui.screen.adding_screen.state_holder

enum class DieselSectionType{
    ACCEPTED, DELIVERY, COEFFICIENT, REFUEL
}

data class DieselSectionFieldState(
    val data: String? = null,
    val type: DieselSectionType
)

sealed class DieselSectionEvent{
    data class EnteredAccepted(val index: Int, val data: String?): DieselSectionEvent()
    data class EnteredDelivery(val index: Int, val data: String?): DieselSectionEvent()
    data class EnteredCoefficient(val index: Int, val data: Double?): DieselSectionEvent()
    data class EnteredRefuel(val index :Int, val data: Double?): DieselSectionEvent()
    data class FocusChange(val index: Int, val fieldName: DieselSectionType): DieselSectionEvent()
}

data class DieselSectionFormState(
    val sectionId: String,
    val accepted: DieselSectionFieldState = DieselSectionFieldState(type = DieselSectionType.ACCEPTED),
    val delivery: DieselSectionFieldState = DieselSectionFieldState(type = DieselSectionType.DELIVERY),
    val coefficient : DieselSectionFieldState = DieselSectionFieldState(type = DieselSectionType.COEFFICIENT),
    val refuel: DieselSectionFieldState = DieselSectionFieldState(type = DieselSectionType.REFUEL),
    val formValid: Boolean,
    val errorMessage: String = ""
)
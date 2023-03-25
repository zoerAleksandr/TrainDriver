package com.example.traindriver.ui.screen.adding_screen.state_holder

sealed class SectionType {
    data class DieselSectionFormState(
        val sectionId: String,
        val accepted: DieselSectionFieldState = DieselSectionFieldState(type = DieselSectionType.ACCEPTED),
        val delivery: DieselSectionFieldState = DieselSectionFieldState(type = DieselSectionType.DELIVERY),
        val coefficient: DieselSectionFieldState = DieselSectionFieldState(type = DieselSectionType.COEFFICIENT),
        val refuel: DieselSectionFieldState = DieselSectionFieldState(type = DieselSectionType.REFUEL),
        val formValid: Boolean,
        val errorMessage: String = ""
    ) : SectionType()

    data class ElectricSectionFormState(
        val sectionId: String
    ) : SectionType()
}
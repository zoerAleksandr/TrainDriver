package com.example.traindriver.ui.screen.adding_screen.adding_train

enum class StationDataType {
    NAME, ARRIVAL, DEPARTURE
}

data class StationField(
    val data: String? = null,
    val type: StationDataType
)

data class StationFieldData(
    val data: Long? = null,
    val type: StationDataType
)

data class StationIsValidField(
    val data: Boolean = false,
    val type: StationDataType? = null
)

data class StationFormState(
    val id: String,
    val station: StationField = StationField(type = StationDataType.NAME),
    val arrival: StationFieldData = StationFieldData(type = StationDataType.ARRIVAL),
    val departure: StationFieldData = StationFieldData(type = StationDataType.DEPARTURE),
    val formValid: StationIsValidField = StationIsValidField(),
    val errorMessage: String = ""
)

sealed class StationEvent{
    data class EnteredStationName(val index: Int, val data: String?): StationEvent()
    data class EnteredArrivalTime(val index: Int, val data: Long?): StationEvent()
    data class EnteredDepartureTime(val index: Int, val data: Long?): StationEvent()
    data class FocusChange(val index: Int, val fieldName: StationDataType): StationEvent()
}
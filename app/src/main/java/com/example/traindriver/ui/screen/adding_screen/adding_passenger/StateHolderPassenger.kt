package com.example.traindriver.ui.screen.adding_screen.adding_passenger

enum class PassengerDataType {
    NUMBER_TRAIN, STATION_DEPARTURE, STATION_ARRIVAL, TIME_DEPARTURE, TIME_ARRIVAL, NOTES
}

data class PassengerFieldText(
    val data: String? = null,
    val type: PassengerDataType
)

data class PassengerFieldDate(
    val data: Long? = null,
    val type: PassengerDataType
)

data class PassengerIsValidField(
    val data: Boolean = true,
    val type: PassengerDataType? = null
)

data class PassengerFormState(
    val id: String,
    val numberTrain: PassengerFieldText = PassengerFieldText(type = PassengerDataType.NUMBER_TRAIN),
    val stationDeparture: PassengerFieldText = PassengerFieldText(type = PassengerDataType.STATION_DEPARTURE),
    val stationArrival: PassengerFieldText = PassengerFieldText(type = PassengerDataType.STATION_ARRIVAL),
    val timeDeparture: PassengerFieldDate = PassengerFieldDate(type = PassengerDataType.TIME_DEPARTURE),
    val timeArrival: PassengerFieldDate = PassengerFieldDate(type = PassengerDataType.TIME_ARRIVAL),
    val notes: PassengerFieldText = PassengerFieldText(type = PassengerDataType.NOTES),
    val formValid: PassengerIsValidField = PassengerIsValidField(),
    var errorMessage: String = ""
)

sealed class PassengerEvent {
    data class EnteredTrainNumber(val data: String?) : PassengerEvent()
    data class EnteredStationDeparture(val data: String?) : PassengerEvent()
    data class EnteredStationArrival(val data: String?) : PassengerEvent()
    data class EnteredTimeDeparture(val data: Long?) : PassengerEvent()
    data class EnteredTimeArrival(val data: Long?) : PassengerEvent()
    data class EnteredNotes(val data: String?) : PassengerEvent()
    data class FocusChange(val fieldName: PassengerDataType) : PassengerEvent()
}


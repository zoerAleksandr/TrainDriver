package com.example.traindriver.ui.screen.adding_screen.adding_passenger

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import com.example.traindriver.domain.entity.Passenger
import com.example.traindriver.ui.util.collection_util.extension.addOrReplace
import com.example.traindriver.ui.util.long_util.compareWithNullable
import org.koin.core.component.KoinComponent
import kotlin.properties.Delegates

class AddingPassengerViewModel : ViewModel(), KoinComponent {
    private var currentPassenger: Passenger by Delegates.observable(
        initialValue = Passenger()
    ) { _, _, passenger ->
        setTrainNumber(TextFieldValue(passenger.trainNumber ?: ""))
        setStationDeparture(TextFieldValue(passenger.stationDeparture ?: ""))
        setStationArrival(TextFieldValue(passenger.stationArrival ?: ""))
        setTimeDeparture(passenger.timeDeparture)
        setTimeArrival(passenger.timeArrival)
        setNotes(TextFieldValue(passenger.notes ?: ""))
    }

    fun clearField() {
        currentPassenger = currentPassenger.copy(
            trainNumber = null,
            stationDeparture = null,
            stationArrival = null,
            timeArrival = null,
            timeDeparture = null,
            notes = null
        )
        formState = formState.copy(
            formValid = PassengerIsValidField(data = true),
            errorMessage = ""
        )
    }

    fun setData(passenger: Passenger?) {
        passenger?.let {
            currentPassenger = it
        }
    }

    private fun setTrainNumber(newValue: TextFieldValue) {
        formState = formState.copy(
            numberTrain = formState.numberTrain.copy(
                data = newValue.text
            )
        )
    }

    private fun setStationDeparture(newValue: TextFieldValue) {
        formState = formState.copy(
            stationDeparture = formState.stationDeparture.copy(
                data = newValue.text
            )
        )
    }

    private fun setStationArrival(newValue: TextFieldValue) {
        formState = formState.copy(
            stationArrival = formState.stationArrival.copy(
                data = newValue.text
            )
        )
    }

    private fun setTimeDeparture(newValue: Long?) {
        formState = formState.copy(
            timeDeparture = formState.timeDeparture.copy(
                data = newValue
            )
        )
    }

    private fun setTimeArrival(newValue: Long?) {
        formState = formState.copy(
            timeArrival = formState.timeArrival.copy(
                data = newValue
            )
        )
    }

    private fun setNotes(newValue: TextFieldValue) {
        formState = formState.copy(
            notes = formState.notes.copy(
                data = newValue.text
            )
        )
    }

    private fun setValidForm(value: Boolean, type: PassengerDataType) {
        formState = formState.copy(
            formValid = formState.formValid.copy(
                data = value,
                type = type
            )
        )
    }

    var formState by mutableStateOf(PassengerFormState(id = currentPassenger.id))
        private set

    fun addPassengerInRoute(passengerList: SnapshotStateList<Passenger>) {
        currentPassenger.apply {
            trainNumber = formState.numberTrain.data
            stationDeparture = formState.stationDeparture.data
            stationArrival = formState.stationArrival.data
            timeDeparture = formState.timeDeparture.data
            timeArrival = formState.timeArrival.data
            notes = formState.notes.data
        }
        passengerList.addOrReplace(currentPassenger)
        clearField()
    }

    fun createEventPassenger(event: PassengerEvent) {
        onPassengerEvent(event)
    }

    private fun onPassengerEvent(event: PassengerEvent) {
        when (event) {
            is PassengerEvent.EnteredTrainNumber -> {
                setTrainNumber(TextFieldValue(event.data ?: ""))
            }
            is PassengerEvent.EnteredStationDeparture -> {
                setStationDeparture(TextFieldValue(event.data ?: ""))
            }
            is PassengerEvent.EnteredStationArrival -> {
                setStationArrival(TextFieldValue(event.data ?: ""))
            }
            is PassengerEvent.EnteredTimeDeparture -> {
                setTimeDeparture(event.data)
            }
            is PassengerEvent.EnteredTimeArrival -> {
                setTimeArrival(event.data)
            }
            is PassengerEvent.EnteredNotes -> {
                setNotes(TextFieldValue(event.data ?: ""))
            }
            is PassengerEvent.FocusChange -> {
                when (event.fieldName) {
                    PassengerDataType.NUMBER_TRAIN -> {}
                    PassengerDataType.STATION_DEPARTURE -> {}
                    PassengerDataType.STATION_ARRIVAL -> {}
                    PassengerDataType.TIME_DEPARTURE -> {
                        val isValid = formValidPassenger(
                            type = PassengerDataType.TIME_DEPARTURE,
                            inputValue = formState.timeDeparture.data
                        )
                        setValidForm(value = isValid, type = PassengerDataType.TIME_DEPARTURE)
                    }
                    PassengerDataType.TIME_ARRIVAL -> {
                        val isValid = formValidPassenger(
                            type = PassengerDataType.TIME_ARRIVAL,
                            inputValue = formState.timeArrival.data
                        )
                        setValidForm(value = isValid, type = PassengerDataType.TIME_ARRIVAL)

                    }
                    PassengerDataType.NOTES -> {}
                }
            }
        }
    }

    private fun formValidPassenger(
        type: PassengerDataType,
        inputValue: Long?
    ): Boolean {
       return when (type) {
            PassengerDataType.NUMBER_TRAIN -> {
                 true
            }
            PassengerDataType.STATION_DEPARTURE -> {
                 true
            }
            PassengerDataType.STATION_ARRIVAL -> {
                 true
            }
            PassengerDataType.TIME_DEPARTURE -> {
                val arrivalTime = formState.timeArrival.data
                if (!inputValue.compareWithNullable(arrivalTime)) {
                    formState.errorMessage = "Время отправления позже прибытия"
                    return false
                }
                true
            }
            PassengerDataType.TIME_ARRIVAL -> {
                val departureTime = formState.timeDeparture.data
                if (!departureTime.compareWithNullable(inputValue)) {
                    formState.errorMessage = "Время прибытия позже отпраления"
                    return false
                }
                true
            }
            PassengerDataType.NOTES -> {
                 true
            }
        }
    }
}
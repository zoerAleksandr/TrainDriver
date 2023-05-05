package com.example.traindriver.ui.screen.adding_screen.adding_train

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import com.example.traindriver.domain.entity.Locomotive
import com.example.traindriver.domain.entity.Station
import com.example.traindriver.domain.entity.Train
import com.example.traindriver.ui.util.collection_util.extension.addOrReplace
import com.example.traindriver.ui.util.long_util.compareWithNullable
import org.koin.core.component.KoinComponent
import kotlin.properties.Delegates

class AddTrainViewModel : ViewModel(), KoinComponent {
    private var currentTrain: Train by Delegates.observable(
        initialValue = Train()
    ) { _, _, train ->
        setTrainNumber(TextFieldValue(train.number ?: ""))
        setTrainWeight(TextFieldValue("${train.weight ?: ""}"))
        setTrainAxle(TextFieldValue("${train.axle ?: ""}"))
        setTrainConditionalLength(TextFieldValue("${train.conditionalLength ?: ""}"))
        setLocomotiveTrain(train.locomotive)
    }

    var stationListState = mutableStateListOf<StationFormState>()
        private set

    val revealedItemStationIdsList = mutableStateListOf<String>()

    fun onExpandedStationItem(stationId: String) {
        if (revealedItemStationIdsList.contains(stationId)) return
        revealedItemStationIdsList.add(stationId)
    }

    fun onCollapsedStationItem(stationId: String) {
        if (!revealedItemStationIdsList.contains(stationId)) return
        revealedItemStationIdsList.remove(stationId)
    }

    fun addStation(station: Station) {
        stationListState.add(
            StationFormState(
                id = station.id,
                formValid = StationIsValidField()
            )
        )
    }

    fun removeStation(stationFormState: StationFormState) {
        stationListState.remove(stationFormState)
    }

    fun setData(train: Train?) {
        stationListState.clear()
        train?.let {
            currentTrain = train
        }
    }

    fun addTrainInRoute(stateTrainList: SnapshotStateList<Train>) {
        val listStation = mutableListOf<Station>()
        stationListState.forEach { state ->
            listStation.add(
                Station(
                    id = state.id,
                    stationName = state.station.data,
                    timeArrival = state.arrival.data,
                    timeDeparture = state.departure.data
                )
            )
        }

        currentTrain.apply {
            number = numberTrain.text
            weight = weightTrain.text.toIntOrNull()
            axle = axleTrain.text.toIntOrNull()
            conditionalLength = conditionalLengthTrain.text.toIntOrNull()
            locomotive = locomotiveTrain.value
            stations = listStation
        }
        stateTrainList.addOrReplace(currentTrain)
        clearField()
    }

    fun clearField() {
        currentTrain = currentTrain.copy(
            number = null,
            weight = null,
            axle = null,
            conditionalLength = null,
            locomotive = null,
            stations = mutableListOf()
        )
        stationListState.clear()
    }

    var numberTrain by mutableStateOf(TextFieldValue(currentTrain.number ?: ""))
        private set

    fun setTrainNumber(newValue: TextFieldValue) {
        numberTrain = newValue
    }

    var weightTrain by mutableStateOf(TextFieldValue(text = "${currentTrain.weight ?: ""}"))
        private set

    fun setTrainWeight(newValue: TextFieldValue) {
        val value = newValue.text.toIntOrNull()
        value?.let {
            weightTrain = newValue
        }
        if (newValue.text.isEmpty()) {
            weightTrain = TextFieldValue("")
        }
    }

    var axleTrain by mutableStateOf(TextFieldValue(text = "${currentTrain.axle ?: ""}"))
        private set

    fun setTrainAxle(newValue: TextFieldValue) {
        val value = newValue.text.toIntOrNull()
        value?.let {
            axleTrain = newValue
        }
        if (newValue.text.isEmpty()) {
            axleTrain = TextFieldValue("")
        }
    }

    var conditionalLengthTrain by mutableStateOf(TextFieldValue(text = "${currentTrain.axle ?: ""}"))
        private set

    fun setTrainConditionalLength(newValue: TextFieldValue) {
        val value = newValue.text.toIntOrNull()
        value?.let {
            conditionalLengthTrain = newValue
        }
        if (newValue.text.isEmpty()) {
            conditionalLengthTrain = TextFieldValue("")
        }
    }

    var locomotiveTrain = mutableStateOf(currentTrain.locomotive)
        private set

    fun setLocomotiveTrain(locomotive: Locomotive?) {
        locomotiveTrain.value = locomotive
    }

    fun createEventStation(event: StationEvent) {
        onStationEvent(event)
    }

    private fun onStationEvent(event: StationEvent) {
        when (event) {
            is StationEvent.EnteredStationName -> {
                stationListState[event.index] = stationListState[event.index].copy(
                    station = stationListState[event.index].station.copy(
                        data = event.data
                    )
                )
            }
            is StationEvent.EnteredArrivalTime -> {
                stationListState[event.index] = stationListState[event.index].copy(
                    arrival = stationListState[event.index].arrival.copy(
                        data = event.data
                    )
                )
            }
            is StationEvent.EnteredDepartureTime -> {
                stationListState[event.index] = stationListState[event.index].copy(
                    departure = stationListState[event.index].departure.copy(
                        data = event.data
                    )
                )
            }
            is StationEvent.FocusChange -> {
                when (event.fieldName) {
                    StationDataType.NAME -> {
                        stationListState[event.index] = stationListState[event.index].copy(
                            formValid = StationIsValidField()
                        )
                    }
                    StationDataType.ARRIVAL -> {
                        val isValid = formValidStation(
                            index = event.index,
                            inputValue = stationListState[event.index].arrival.data,
                            type = StationDataType.ARRIVAL
                        )
                        stationListState[event.index] = stationListState[event.index].copy(
                            formValid = StationIsValidField(
                                data = isValid,
                                type = StationDataType.ARRIVAL
                            )
                        )
                    }
                    StationDataType.DEPARTURE -> {
                        val isValid = formValidStation(
                            index = event.index,
                            inputValue = stationListState[event.index].departure.data,
                            type = StationDataType.DEPARTURE
                        )
                        stationListState[event.index] = stationListState[event.index].copy(
                            formValid = StationIsValidField(
                                data = isValid,
                                type = StationDataType.DEPARTURE
                            )
                        )
                    }
                }
            }
        }
    }

    private fun formValidStation(
        index: Int,
        inputValue: Long?,
        type: StationDataType
    ): Boolean {
        return when(type){
            StationDataType.NAME -> true
            StationDataType.ARRIVAL -> {
                val departure = stationListState[index].departure.data
                return inputValue.compareWithNullable(departure)
            }
            StationDataType.DEPARTURE -> {
                val arrival = stationListState[index].arrival.data
                return arrival.compareWithNullable(inputValue)
            }
        }
    }
}
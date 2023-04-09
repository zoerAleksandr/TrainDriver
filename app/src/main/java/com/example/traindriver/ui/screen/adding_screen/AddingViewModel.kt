package com.example.traindriver.ui.screen.adding_screen

import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.traindriver.data.repository.DataStoreRepository
import com.example.traindriver.domain.entity.Locomotive
import com.example.traindriver.ui.screen.adding_screen.state_holder.*
import com.example.traindriver.ui.util.long_util.minus
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AddingViewModel : ViewModel(), KoinComponent {
    private val dataStoreRepository: DataStoreRepository by inject()
    private var _timeEditState = mutableStateOf(WorkTimeEditState(formValid = true))
    val timeEditState: State<WorkTimeEditState> = _timeEditState

    var restState by mutableStateOf(false)
        private set

    fun setRest(rest: Boolean) {
        restState = rest
    }

    var numberRouteState by mutableStateOf(TextFieldValue(""))
        private set

    fun setNumber(newValue: TextFieldValue) {
        numberRouteState = newValue
    }

    val stateLocoList = mutableStateListOf<Locomotive>()
    fun deleteLocomotiveInRoute(
        locomotive: Locomotive
    ) {
        Log.d("ZZZ", "state in AddRouteViewModel = ${stateLocoList.hashCode()}")
        if (stateLocoList.contains(locomotive)) {
            stateLocoList.remove(locomotive)
        }
    }

    var minTimeRest by mutableStateOf(0L)
        private set

    fun createEvent(event: WorkTimeEvent) {
        onEvent(event)
    }

    private fun onEvent(event: WorkTimeEvent) {
        when (event) {
            is WorkTimeEvent.EnteredStartTime -> {
                _timeEditState.value = timeEditState.value.copy(
                    startTime = timeEditState.value.startTime.copy(
                        time = event.value
                    )
                )
            }
            is WorkTimeEvent.EnteredEndTime -> {
                _timeEditState.value = timeEditState.value.copy(
                    endTime = timeEditState.value.endTime.copy(
                        time = event.value
                    )
                )
            }
            is WorkTimeEvent.FocusChange -> {
                when (event.fieldName) {
                    WorkTimeType.START -> {
                        val timeValid =
                            validateInput(timeEditState.value.startTime.time, WorkTimeType.START)
                        _timeEditState.value = timeEditState.value.copy(
                            formValid = timeValid
                        )
                    }
                    WorkTimeType.END -> {
                        val timeValid =
                            validateInput(timeEditState.value.endTime.time, WorkTimeType.END)
                        _timeEditState.value = timeEditState.value.copy(
                            formValid = timeValid
                        )
                    }
                }
            }
        }
    }

    private fun validateInput(inputValue: Long?, type: WorkTimeType): Boolean {
        when (type) {
            WorkTimeType.START -> {
                val end = timeEditState.value.endTime.time
                val result = end - inputValue
                result?.let {
                    return it >= 0
                } ?: return true
            }
            WorkTimeType.END -> {
                val start = timeEditState.value.startTime.time
                val result = inputValue - start
                result?.let {
                    return it >= 0
                } ?: return true
            }
        }
    }

    fun getMinTimeRest() {
        viewModelScope.launch {
            minTimeRest = dataStoreRepository.getMinTimeRest().first()
        }
    }
}
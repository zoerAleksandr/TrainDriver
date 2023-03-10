package com.example.traindriver.ui.screen.adding_screen

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.traindriver.data.repository.DataStoreRepository
import com.example.traindriver.domain.entity.Locomotive
import com.example.traindriver.ui.util.long_util.minus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AddingViewModel : ViewModel(), KoinComponent {
    private val dataStoreRepository: DataStoreRepository by inject()
    private var _state = mutableStateOf(WorkTimeEditState(formValid = true))
    val state: State<WorkTimeEditState> = _state

    private val _stateLocoList = mutableStateOf(listOf<Locomotive>(
        Locomotive(series = "2эс4к", number = "103"),
        Locomotive(series = "3эс4к", number = "078"),
        Locomotive(series = "ВЛ10", number = "1010"),
        Locomotive(series = "3эс4к", number = "078"),
        Locomotive(series = "ВЛ10", number = "1010")
    ))
    val stateLocoList: State<List<Locomotive>> = _stateLocoList

    var minTimeRest by mutableStateOf(0L)
        private set

    fun createEvent(event: WorkTimeEvent) {
        onEvent(event)
    }

    private fun onEvent(event: WorkTimeEvent) {
        when (event) {
            is WorkTimeEvent.EnteredStartTime -> {
                _state.value = state.value.copy(
                    startTime = state.value.startTime.copy(
                        time = event.value
                    )
                )
            }
            is WorkTimeEvent.EnteredEndTime -> {
                _state.value = state.value.copy(
                    endTime = state.value.endTime.copy(
                        time = event.value
                    )
                )
            }
            is WorkTimeEvent.FocusChange -> {
                when (event.fieldName) {
                    WorkTimeType.START -> {
                        val timeValid =
                            validateInput(state.value.startTime.time, WorkTimeType.START)
                        _state.value = state.value.copy(
                            formValid = timeValid
                        )
                    }
                    WorkTimeType.END -> {
                        val timeValid = validateInput(state.value.endTime.time, WorkTimeType.END)
                        _state.value = state.value.copy(
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
                val end = state.value.endTime.time
                val result = end - inputValue
                result?.let {
                    return it >= 0
                } ?: return true
            }
            WorkTimeType.END -> {
                val start = state.value.startTime.time
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

    val addLocomotive: (Locomotive) -> Unit = {
        val list = _stateLocoList.value.toMutableList()
        list.add(0, it)
        _stateLocoList.value = list
        Log.d("ZZZ", "${_stateLocoList.value.javaClass}")
    }
}


sealed class WorkTimeEvent {
    data class EnteredStartTime(val value: Long?) : WorkTimeEvent()
    data class EnteredEndTime(val value: Long?) : WorkTimeEvent()
    data class FocusChange(val fieldName: WorkTimeType) : WorkTimeEvent()
}

enum class WorkTimeType {
    START, END
}

data class WorkTimeState(
    val time: Long? = null,
    val type: WorkTimeType,
)

data class WorkTimeEditState(
    val startTime: WorkTimeState = WorkTimeState(type = WorkTimeType.START),
    val endTime: WorkTimeState = WorkTimeState(type = WorkTimeType.END),
    val formValid: Boolean,
    val errorMessage: String = "Время явки позже сдачи"
)
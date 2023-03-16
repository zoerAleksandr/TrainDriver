package com.example.traindriver.ui.screen.adding_screen

import androidx.compose.runtime.*
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.traindriver.data.repository.DataStoreRepository
import com.example.traindriver.domain.entity.Locomotive
import com.example.traindriver.domain.entity.SectionDiesel
import com.example.traindriver.domain.entity.SectionElectric
import com.example.traindriver.ui.screen.adding_screen.state_holder.*
import com.example.traindriver.ui.util.DateAndTimeFormat
import com.example.traindriver.ui.util.long_util.minus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.text.SimpleDateFormat
import java.util.*

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

    private val _stateLocoList = mutableStateOf(
        listOf(
            Locomotive(series = "2эс4к", number = "103"),
            Locomotive(series = "3эс4к", number = "078")
        )
    )
    val stateLocoList: State<List<Locomotive>> = _stateLocoList

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

    val addLocomotive: (Locomotive) -> Unit = {
        val list = _stateLocoList.value.toMutableList()
        list.add(0, it)
        _stateLocoList.value = list
    }

    var acceptedTimeState = mutableStateOf(AcceptedBlockState(formValid = true))
        private set

    fun createEventAccepted(event: AcceptedEvent) {
        onAcceptedEvent(event)
    }

    private fun onAcceptedEvent(event: AcceptedEvent) {
        when (event) {
            is AcceptedEvent.EnteredStartAccepted -> {
                acceptedTimeState.value = acceptedTimeState.value.copy(
                    startAccepted = acceptedTimeState.value.startAccepted.copy(
                        time = event.value
                    )
                )
            }
            is AcceptedEvent.EnteredEndAccepted -> {
                acceptedTimeState.value = acceptedTimeState.value.copy(
                    endAccepted = acceptedTimeState.value.endAccepted.copy(
                        time = event.value
                    )
                )
            }
            is AcceptedEvent.FocusChange -> {
                when (event.fieldName) {
                    AcceptedType.START -> {
                        val valid = validateAccepted(
                            acceptedTimeState.value.startAccepted.time,
                            AcceptedType.START
                        )
                        acceptedTimeState.value = acceptedTimeState.value.copy(
                            formValid = valid
                        )
                    }
                    AcceptedType.END -> {
                        val valid = validateAccepted(
                            acceptedTimeState.value.endAccepted.time,
                            AcceptedType.END
                        )
                        acceptedTimeState.value = acceptedTimeState.value.copy(
                            formValid = valid
                        )
                    }
                }
            }
        }
    }

    private fun validateAccepted(inputValue: Long?, type: AcceptedType): Boolean {
        acceptedTimeState.value.endAccepted.time?.let { endAccepted ->
            deliveryTimeState.value.startDelivered.time?.let { startDelivery ->
                if (startDelivery > endAccepted) {
                    acceptedTimeState.value.errorMessage =
                        "Начало приемки позже окончания"
                    return false
                }
            }
        }
        return when (type) {
            AcceptedType.START -> {
                inputValue?.let { input ->
                    timeEditState.value.startTime.time?.let { timeStart ->
                        if (input < timeStart) {
                            val timeStartText =
                                SimpleDateFormat(
                                    DateAndTimeFormat.TIME_FORMAT,
                                    Locale.getDefault()
                                ).format(timeStart)
                            acceptedTimeState.value.errorMessage =
                                "Время приемки раньше явки $timeStartText"
                            return false
                        }
                    }
                    timeEditState.value.endTime.time?.let { endTime ->
                        if (input > endTime) {
                            val timeEndText =
                                SimpleDateFormat(
                                    DateAndTimeFormat.TIME_FORMAT,
                                    Locale.getDefault()
                                ).format(endTime)
                            acceptedTimeState.value.errorMessage =
                                "Время приемки позже окончания работы $timeEndText"
                            return false
                        }
                    }
                    acceptedTimeState.value.endAccepted.time?.let { endAccepted ->
                        if (input > endAccepted) {
                            acceptedTimeState.value.errorMessage =
                                "Начало приемки позже окончания"
                            return false
                        }
                    }
                }
                true
            }
            AcceptedType.END -> {
                inputValue?.let { input ->
                    timeEditState.value.startTime.time?.let { timeStart ->
                        if (input < timeStart) {
                            val timeStartText =
                                SimpleDateFormat(
                                    DateAndTimeFormat.TIME_FORMAT,
                                    Locale.getDefault()
                                ).format(timeStart)
                            acceptedTimeState.value.errorMessage =
                                "Время приемки раньше явки $timeStartText"
                            return false
                        }
                    }
                    timeEditState.value.endTime.time?.let { endTime ->
                        if (input > endTime) {
                            val timeEndText =
                                SimpleDateFormat(
                                    DateAndTimeFormat.TIME_FORMAT,
                                    Locale.getDefault()
                                ).format(endTime)
                            acceptedTimeState.value.errorMessage =
                                "Время приемки позже окончания работы $timeEndText"
                            return false
                        }
                    }
                    acceptedTimeState.value.startAccepted.time?.let { startAccepted ->
                        if (input < startAccepted) {
                            acceptedTimeState.value.errorMessage =
                                "Окончание приемки раньше начала"
                            return false
                        }
                    }
                }
                true
            }
        }
    }

    var deliveryTimeState = mutableStateOf(DeliveryBlockState(formValid = true))
        private set

    fun createEventDelivery(event: DeliveryEvent) {
        onDeliveryEvent(event)
    }

    private fun onDeliveryEvent(event: DeliveryEvent) {
        when (event) {
            is DeliveryEvent.EnteredStartDelivery -> {
                deliveryTimeState.value = deliveryTimeState.value.copy(
                    startDelivered = deliveryTimeState.value.startDelivered.copy(
                        time = event.value
                    )
                )
            }
            is DeliveryEvent.EnteredEndDelivery -> {
                deliveryTimeState.value = deliveryTimeState.value.copy(
                    endDelivered = deliveryTimeState.value.endDelivered.copy(
                        time = event.value
                    )
                )
            }
            is DeliveryEvent.FocusChange -> {
                when (event.fieldName) {
                    DeliveredType.START -> {
                        val valid = validateDelivery(
                            deliveryTimeState.value.startDelivered.time,
                            DeliveredType.START
                        )
                        deliveryTimeState.value = deliveryTimeState.value.copy(
                            formValid = valid
                        )
                    }
                    DeliveredType.END -> {
                        val valid = validateDelivery(
                            deliveryTimeState.value.endDelivered.time,
                            DeliveredType.END
                        )
                        deliveryTimeState.value = deliveryTimeState.value.copy(
                            formValid = valid
                        )
                    }
                }
            }
        }
    }

    private fun validateDelivery(inputValue: Long?, type: DeliveredType): Boolean {
        acceptedTimeState.value.endAccepted.time?.let { endAccepted ->
            deliveryTimeState.value.startDelivered.time?.let { startDelivery ->
                if (startDelivery < endAccepted) {
                    deliveryTimeState.value.errorMessage =
                        "Начало сдачи раньше окончания приемки"
                    return false
                }
            }
        }
        return when (type) {
            DeliveredType.START -> {
                inputValue?.let { input ->
                    timeEditState.value.startTime.time?.let { timeStart ->
                        if (input < timeStart) {
                            val timeStartText =
                                SimpleDateFormat(
                                    DateAndTimeFormat.TIME_FORMAT,
                                    Locale.getDefault()
                                ).format(timeStart)
                            deliveryTimeState.value.errorMessage =
                                "Время сдачи раньше явки $timeStartText"
                            return false
                        }
                    }
                    timeEditState.value.endTime.time?.let { timeEnd ->
                        if (input > timeEnd) {
                            val timeStartText =
                                SimpleDateFormat(
                                    DateAndTimeFormat.TIME_FORMAT,
                                    Locale.getDefault()
                                ).format(timeEnd)
                            deliveryTimeState.value.errorMessage =
                                "Время сдачи позже окончания работы $timeStartText"
                            return false
                        }
                    }
                    deliveryTimeState.value.endDelivered.time?.let { endDelivery ->
                        if (input > endDelivery) {
                            deliveryTimeState.value.errorMessage =
                                "Начало сдачи позже окончания"
                            return false
                        }
                    }
                }
                true
            }
            DeliveredType.END -> {
                inputValue?.let { input ->
                    timeEditState.value.startTime.time?.let { timeStart ->
                        if (input < timeStart) {
                            val timeStartText =
                                SimpleDateFormat(
                                    DateAndTimeFormat.TIME_FORMAT,
                                    Locale.getDefault()
                                ).format(timeStart)
                            deliveryTimeState.value.errorMessage =
                                "Время сдачи раньше явки $timeStartText"
                            return false
                        }
                    }
                    timeEditState.value.endTime.time?.let { timeEnd ->
                        if (input > timeEnd) {
                            val timeStartText =
                                SimpleDateFormat(
                                    DateAndTimeFormat.TIME_FORMAT,
                                    Locale.getDefault()
                                ).format(timeEnd)
                            deliveryTimeState.value.errorMessage =
                                "Время сдачи позже окончания работы $timeStartText"
                            return false
                        }
                    }
                    deliveryTimeState.value.startDelivered.time?.let { startDelivery ->
                        if (input < startDelivery) {
                            deliveryTimeState.value.errorMessage =
                                "Окончание сдачи раньше начала "
                            return false
                        }
                    }
                }
                true
            }
        }
    }

    var dieselSectionListState = mutableStateOf(listOf(SectionDiesel()))
        private set

    var electricSectionListState = mutableStateOf(listOf<SectionElectric>())
        private set

    fun addDieselSection() {
        val list = dieselSectionListState.value.toMutableList()
        list.add(SectionDiesel())
        dieselSectionListState.value = list
    }

    fun addElectricSection() {
        val list = electricSectionListState.value.toMutableList()
        list.add(SectionElectric())
        electricSectionListState.value = list
    }
}
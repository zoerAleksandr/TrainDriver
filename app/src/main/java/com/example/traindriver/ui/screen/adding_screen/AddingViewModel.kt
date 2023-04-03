package com.example.traindriver.ui.screen.adding_screen

import androidx.compose.runtime.*
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.traindriver.data.repository.DataStoreRepository
import com.example.traindriver.domain.entity.Calculation
import com.example.traindriver.domain.entity.Locomotive
import com.example.traindriver.domain.entity.SectionDiesel
import com.example.traindriver.domain.entity.SectionElectric
import com.example.traindriver.ui.screen.adding_screen.state_holder.*
import com.example.traindriver.ui.util.DateAndTimeFormat
import com.example.traindriver.ui.util.double_util.str
import com.example.traindriver.ui.util.long_util.minus
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
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

    var seriesLocoState by mutableStateOf(TextFieldValue(""))
        private set

    fun setSeriesLoco(newValue: TextFieldValue) {
        seriesLocoState = newValue
    }

    var numberLocoState by mutableStateOf(TextFieldValue(""))
        private set

    fun setNumberLoco(newValue: TextFieldValue) {
        numberLocoState = newValue
    }

    var pagerState by mutableStateOf(0)
        private set

    private suspend fun getTypeLoco(): Boolean =
        withContext(viewModelScope.coroutineContext) {
            dataStoreRepository.getTypeLoco().first()
        }

    init {
        viewModelScope.launch {
            pagerState = if (getTypeLoco()) 1 else 0
        }
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
                            acceptedTimeState.value.startAccepted.time, AcceptedType.START
                        )
                        acceptedTimeState.value = acceptedTimeState.value.copy(
                            formValid = valid
                        )
                    }
                    AcceptedType.END -> {
                        val valid = validateAccepted(
                            acceptedTimeState.value.endAccepted.time, AcceptedType.END
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
                    acceptedTimeState.value.errorMessage = "Начало приемки позже окончания"
                    return false
                }
            }
        }
        return when (type) {
            AcceptedType.START -> {
                inputValue?.let { input ->
                    timeEditState.value.startTime.time?.let { timeStart ->
                        if (input < timeStart) {
                            val timeStartText = SimpleDateFormat(
                                DateAndTimeFormat.TIME_FORMAT, Locale.getDefault()
                            ).format(timeStart)
                            acceptedTimeState.value.errorMessage =
                                "Время приемки раньше явки $timeStartText"
                            return false
                        }
                    }
                    timeEditState.value.endTime.time?.let { endTime ->
                        if (input > endTime) {
                            val timeEndText = SimpleDateFormat(
                                DateAndTimeFormat.TIME_FORMAT, Locale.getDefault()
                            ).format(endTime)
                            acceptedTimeState.value.errorMessage =
                                "Время приемки позже окончания работы $timeEndText"
                            return false
                        }
                    }
                    acceptedTimeState.value.endAccepted.time?.let { endAccepted ->
                        if (input > endAccepted) {
                            acceptedTimeState.value.errorMessage = "Начало приемки позже окончания"
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
                            val timeStartText = SimpleDateFormat(
                                DateAndTimeFormat.TIME_FORMAT, Locale.getDefault()
                            ).format(timeStart)
                            acceptedTimeState.value.errorMessage =
                                "Время приемки раньше явки $timeStartText"
                            return false
                        }
                    }
                    timeEditState.value.endTime.time?.let { endTime ->
                        if (input > endTime) {
                            val timeEndText = SimpleDateFormat(
                                DateAndTimeFormat.TIME_FORMAT, Locale.getDefault()
                            ).format(endTime)
                            acceptedTimeState.value.errorMessage =
                                "Время приемки позже окончания работы $timeEndText"
                            return false
                        }
                    }
                    acceptedTimeState.value.startAccepted.time?.let { startAccepted ->
                        if (input < startAccepted) {
                            acceptedTimeState.value.errorMessage = "Окончание приемки раньше начала"
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
                            deliveryTimeState.value.startDelivered.time, DeliveredType.START
                        )
                        deliveryTimeState.value = deliveryTimeState.value.copy(
                            formValid = valid
                        )
                    }
                    DeliveredType.END -> {
                        val valid = validateDelivery(
                            deliveryTimeState.value.endDelivered.time, DeliveredType.END
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
                    deliveryTimeState.value.errorMessage = "Начало сдачи раньше окончания приемки"
                    return false
                }
            }
        }
        return when (type) {
            DeliveredType.START -> {
                inputValue?.let { input ->
                    timeEditState.value.startTime.time?.let { timeStart ->
                        if (input < timeStart) {
                            val timeStartText = SimpleDateFormat(
                                DateAndTimeFormat.TIME_FORMAT, Locale.getDefault()
                            ).format(timeStart)
                            deliveryTimeState.value.errorMessage =
                                "Время сдачи раньше явки $timeStartText"
                            return false
                        }
                    }
                    timeEditState.value.endTime.time?.let { timeEnd ->
                        if (input > timeEnd) {
                            val timeStartText = SimpleDateFormat(
                                DateAndTimeFormat.TIME_FORMAT, Locale.getDefault()
                            ).format(timeEnd)
                            deliveryTimeState.value.errorMessage =
                                "Время сдачи позже окончания работы $timeStartText"
                            return false
                        }
                    }
                    deliveryTimeState.value.endDelivered.time?.let { endDelivery ->
                        if (input > endDelivery) {
                            deliveryTimeState.value.errorMessage = "Начало сдачи позже окончания"
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
                            val timeStartText = SimpleDateFormat(
                                DateAndTimeFormat.TIME_FORMAT, Locale.getDefault()
                            ).format(timeStart)
                            deliveryTimeState.value.errorMessage =
                                "Время сдачи раньше явки $timeStartText"
                            return false
                        }
                    }
                    timeEditState.value.endTime.time?.let { timeEnd ->
                        if (input > timeEnd) {
                            val timeStartText = SimpleDateFormat(
                                DateAndTimeFormat.TIME_FORMAT, Locale.getDefault()
                            ).format(timeEnd)
                            deliveryTimeState.value.errorMessage =
                                "Время сдачи позже окончания работы $timeStartText"
                            return false
                        }
                    }
                    deliveryTimeState.value.startDelivered.time?.let { startDelivery ->
                        if (input < startDelivery) {
                            deliveryTimeState.value.errorMessage = "Окончание сдачи раньше начала "
                            return false
                        }
                    }
                }
                true
            }
        }
    }

    var dieselSectionListState = mutableStateListOf<SectionType.DieselSectionFormState>()
        private set

    var revealedItemDieselSectionIdsList = mutableStateListOf<String>()
        private set

    fun onExpandedDieselSection(sectionId: String) {
        if (revealedItemDieselSectionIdsList.contains(sectionId)) return
        revealedItemDieselSectionIdsList.add(sectionId)
    }

    fun onCollapsedDieselSection(sectionId: String) {
        if (!revealedItemDieselSectionIdsList.contains(sectionId)) return
        revealedItemDieselSectionIdsList.remove(sectionId)
    }

    fun addDieselSection(sectionDiesel: SectionDiesel) {
        viewModelScope.launch {
            dieselSectionListState.add(
                SectionType.DieselSectionFormState(
                    sectionId = sectionDiesel.id,
                    formValid = true,
                    coefficient = DieselSectionFieldState(
                        type = DieselSectionType.COEFFICIENT,
                        data = getCoefficient().str()
                    )
                )
            )
        }
    }

    fun removeDieselSection(sectionState: SectionType.DieselSectionFormState) {
        dieselSectionListState.remove(sectionState)
    }

    fun createEventDieselSection(event: DieselSectionEvent) {
        onDieselSectionEvent(event)
    }

    private fun onDieselSectionEvent(event: DieselSectionEvent) {
        when (event) {
            is DieselSectionEvent.EnteredAccepted -> {
                dieselSectionListState[event.index] = dieselSectionListState[event.index].copy(
                    accepted = dieselSectionListState[event.index].accepted.copy(
                        data = event.data
                    )
                )
            }
            is DieselSectionEvent.EnteredDelivery -> {
                dieselSectionListState[event.index] = dieselSectionListState[event.index].copy(
                    delivery = dieselSectionListState[event.index].delivery.copy(
                        data = event.data
                    )
                )
            }
            is DieselSectionEvent.EnteredCoefficient -> {
                dieselSectionListState[event.index] = dieselSectionListState[event.index].copy(
                    coefficient = dieselSectionListState[event.index].coefficient.copy(
                        data = event.data?.str()
                    )
                )
            }
            is DieselSectionEvent.EnteredRefuel -> {
                dieselSectionListState[event.index] = dieselSectionListState[event.index].copy(
                    refuel = dieselSectionListState[event.index].coefficient.copy(
                        data = event.data?.str()
                    )
                )
            }

            is DieselSectionEvent.FocusChange -> {
                when (event.fieldName) {
                    DieselSectionType.ACCEPTED -> {
                        val isValid = validateDieselSection(
                            index = event.index,
                            inputValue = dieselSectionListState[event.index].accepted.data,
                            type = DieselSectionType.ACCEPTED
                        )
                        dieselSectionListState[event.index] =
                            dieselSectionListState[event.index].copy(
                                formValid = isValid
                            )
                    }
                    DieselSectionType.DELIVERY -> {
                        val isValid = validateDieselSection(
                            index = event.index,
                            inputValue = dieselSectionListState[event.index].delivery.data,
                            type = DieselSectionType.DELIVERY
                        )
                        dieselSectionListState[event.index] =
                            dieselSectionListState[event.index].copy(
                                formValid = isValid
                            )
                    }
                    DieselSectionType.COEFFICIENT -> {
                        val isValid = validateDieselSection(
                            index = event.index,
                            inputValue = dieselSectionListState[event.index].coefficient.data,
                            type = DieselSectionType.COEFFICIENT
                        )
                        dieselSectionListState[event.index] =
                            dieselSectionListState[event.index].copy(
                                formValid = isValid
                            )
                    }
                    DieselSectionType.REFUEL -> {
                        val isValid = validateDieselSection(
                            index = event.index,
                            inputValue = dieselSectionListState[event.index].refuel.data,
                            type = DieselSectionType.REFUEL
                        )
                        dieselSectionListState[event.index] =
                            dieselSectionListState[event.index].copy(
                                formValid = isValid
                            )
                    }
                }
            }
        }
    }

    private fun validateDieselSection(
        index: Int,
        inputValue: String?,
        type: DieselSectionType
    ): Boolean {
        return when (type) {
            DieselSectionType.ACCEPTED -> {
                val accepted = inputValue?.toDoubleOrNull()
                val delivery = dieselSectionListState[index].delivery.data?.toDoubleOrNull()
                val refuel = dieselSectionListState[index].refuel.data?.toDoubleOrNull()

                val result = Calculation.getTotalFuelConsumption(accepted, delivery, refuel)
                result?.let {
                    if (it < 0) {
                        dieselSectionListState[index] = dieselSectionListState[index].copy(
                            errorMessage = "Принял меньше чем сдал"
                        )
                        return false
                    }
                }
                true
            }
            DieselSectionType.DELIVERY -> {
                val accepted = dieselSectionListState[index].accepted.data?.toDoubleOrNull()
                val delivery = inputValue?.toDoubleOrNull()
                val refuel = dieselSectionListState[index].refuel.data?.toDoubleOrNull()

                val result = Calculation.getTotalFuelConsumption(accepted, delivery, refuel)
                result?.let {
                    if (it < 0) {
                        dieselSectionListState[index] = dieselSectionListState[index].copy(
                            errorMessage = "Сдал больше чем принял"
                        )
                        return false
                    }
                }
                true
            }
            DieselSectionType.COEFFICIENT -> {
                val coefficient = inputValue?.toDoubleOrNull()
                coefficient?.let {
                    if (it > 1) {
                        dieselSectionListState[index] = dieselSectionListState[index].copy(
                            errorMessage = "Коэффициент больше 1.0"
                        )
                        return false
                    }
                }
                true
            }
            DieselSectionType.REFUEL -> {
                val accepted = dieselSectionListState[index].accepted.data?.toDoubleOrNull()
                val delivery = dieselSectionListState[index].delivery.data?.toDoubleOrNull()
                val refuel = inputValue?.toDoubleOrNull()

                val result = Calculation.getTotalFuelConsumption(accepted, delivery, refuel)
                result?.let {
                    if (it < 0) {
                        dieselSectionListState[index] = dieselSectionListState[index].copy(
                            errorMessage = "Не хватает экипировки"
                        )
                        return false
                    }
                }
                true
            }
        }
    }

    private suspend fun getCoefficient(): Double =
        withContext(viewModelScope.coroutineContext) {
            dataStoreRepository.readDieselCoefficient().first()
        }

    var electricSectionListState = mutableStateListOf<SectionType.ElectricSectionFormState>()
        private set

    var revealedItemElectricSectionIdsList = mutableStateListOf<String>()
        private set

    fun onExpandedElectricSection(sectionId: String) {
        if (revealedItemElectricSectionIdsList.contains(sectionId)) return
        revealedItemElectricSectionIdsList.add(sectionId)
    }

    fun onCollapsedElectricSection(sectionId: String) {
        if (!revealedItemElectricSectionIdsList.contains(sectionId)) return
        revealedItemElectricSectionIdsList.remove(sectionId)
    }

    fun createEventElectricSection(event: ElectricSectionEvent) {
        onElectricSectionEvent(event)
    }

    private fun onElectricSectionEvent(event: ElectricSectionEvent) {
        when (event) {
            is ElectricSectionEvent.EnteredAccepted -> {
                electricSectionListState[event.index] = electricSectionListState[event.index].copy(
                    accepted = electricSectionListState[event.index].accepted.copy(
                        data = event.data
                    )
                )
            }
            is ElectricSectionEvent.EnteredDelivery -> {
                electricSectionListState[event.index] = electricSectionListState[event.index].copy(
                    delivery = electricSectionListState[event.index].delivery.copy(
                        data = event.data
                    )
                )
            }
            is ElectricSectionEvent.EnteredRecoveryAccepted -> {
                electricSectionListState[event.index] = electricSectionListState[event.index].copy(
                    recoveryAccepted = electricSectionListState[event.index].recoveryAccepted.copy(
                        data = event.data
                    )
                )
            }
            is ElectricSectionEvent.EnteredRecoveryDelivery -> {
                electricSectionListState[event.index] = electricSectionListState[event.index].copy(
                    recoveryDelivery = electricSectionListState[event.index].recoveryDelivery.copy(
                        data = event.data
                    )
                )
            }
            is ElectricSectionEvent.FocusChange -> {
                when (event.fieldName) {
                    ElectricSectionType.ACCEPTED -> {
                        val isValid = validateElectricSection(
                            index = event.index,
                            inputValue = electricSectionListState[event.index].accepted.data,
                            type = ElectricSectionType.ACCEPTED
                        )

                        electricSectionListState[event.index] =
                            electricSectionListState[event.index].copy(
                                formValid = isValid
                            )
                    }
                    ElectricSectionType.DELIVERY -> {
                        val isValid = validateElectricSection(
                            index = event.index,
                            inputValue = electricSectionListState[event.index].delivery.data,
                            type = ElectricSectionType.DELIVERY
                        )

                        electricSectionListState[event.index] =
                            electricSectionListState[event.index].copy(
                                formValid = isValid
                            )
                    }
                    ElectricSectionType.RECOVERY_ACCEPTED -> {
                        val isValid = validateElectricSection(
                            index = event.index,
                            inputValue = electricSectionListState[event.index].recoveryAccepted.data,
                            type = ElectricSectionType.RECOVERY_ACCEPTED
                        )

                        electricSectionListState[event.index] =
                            electricSectionListState[event.index].copy(
                                formValid = isValid
                            )
                    }
                    ElectricSectionType.RECOVERY_DELIVERY -> {
                        val isValid = validateElectricSection(
                            index = event.index,
                            inputValue = electricSectionListState[event.index].recoveryDelivery.data,
                            type = ElectricSectionType.RECOVERY_DELIVERY
                        )

                        electricSectionListState[event.index] =
                            electricSectionListState[event.index].copy(
                                formValid = isValid
                            )
                    }
                }
            }
        }
    }

    private fun validateElectricSection(
        index: Int,
        inputValue: String?,
        type: ElectricSectionType
    ): Boolean {
        return when (type) {

            ElectricSectionType.ACCEPTED -> {
                val accepted = inputValue?.toDoubleOrNull()
                val delivery = electricSectionListState[index].delivery.data?.toDoubleOrNull()

                delivery?.let { del ->
                    accepted?.let { acc ->
                        if (acc > del) {
                            electricSectionListState[index] = electricSectionListState[index].copy(
                                errorMessage = "Принято больше чем сдано"
                            )
                            return false
                        }
                    }
                }
                true
            }

            ElectricSectionType.DELIVERY -> {
                val accepted = electricSectionListState[index].accepted.data?.toDoubleOrNull()
                val delivery = inputValue?.toDoubleOrNull()

                accepted?.let { acc ->
                    delivery?.let { del ->
                        if (del < acc) {
                            electricSectionListState[index] = electricSectionListState[index].copy(
                                errorMessage = "Сдано меньше чем принято"
                            )
                            return false
                        }
                    }
                }
                true
            }

            ElectricSectionType.RECOVERY_ACCEPTED -> {
                val accepted = inputValue?.toDoubleOrNull()
                val delivery =
                    electricSectionListState[index].recoveryDelivery.data?.toDoubleOrNull()

                delivery?.let { del ->
                    accepted?.let { acc ->
                        if (acc > del) {
                            electricSectionListState[index] = electricSectionListState[index].copy(
                                errorMessage = "Принято больше чем сдано"
                            )
                            return false
                        }
                    }
                }
                true
            }

            ElectricSectionType.RECOVERY_DELIVERY -> {
                val accepted =
                    electricSectionListState[index].recoveryAccepted.data?.toDoubleOrNull()
                val delivery = inputValue?.toDoubleOrNull()

                accepted?.let { acc ->
                    delivery?.let { del ->
                        if (del < acc) {
                            electricSectionListState[index] = electricSectionListState[index].copy(
                                errorMessage = "Сдано меньше чем принято"
                            )
                            return false
                        }
                    }
                }
                true
            }
        }
    }

    fun addElectricSection(sectionElectric: SectionElectric) {
        viewModelScope.launch {
            electricSectionListState.add(
                SectionType.ElectricSectionFormState(
                    sectionId = sectionElectric.id,
                    formValid = true
                )
            )
        }
    }

    fun removeElectricSection(sectionState: SectionType.ElectricSectionFormState) {
        electricSectionListState.remove(sectionState)
    }
}
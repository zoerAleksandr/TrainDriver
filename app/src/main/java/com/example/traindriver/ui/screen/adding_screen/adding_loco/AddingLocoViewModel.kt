package com.example.traindriver.ui.screen.adding_screen.adding_loco

import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
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
import com.example.traindriver.ui.util.collection_util.extension.addOrReplace
import com.example.traindriver.ui.util.double_util.str
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates

class AddingLocoViewModel : ViewModel(), KoinComponent {
    private val dataStoreRepository: DataStoreRepository by inject()

    fun setData(
        locomotive: Locomotive?,
        timeState: State<WorkTimeEditState>
    ) {
        workTimeState = timeState
        dieselSectionListState.clear()
        electricSectionListState.clear()
        locomotive?.let {
            currentLoco = it
        }
    }

    fun clearField() {
        currentLoco = currentLoco.copy(
            series = null,
            number = null,
            type = true,
            sectionList = listOf(),
            timeStartOfAcceptance = null,
            timeEndOfAcceptance = null,
            timeStartOfDelivery = null,
            timeEndOfDelivery = null
        )
        dieselSectionListState.clear()
        electricSectionListState.clear()
    }

    fun addLocomotiveInRoute(stateLocomotive: SnapshotStateList<Locomotive>) {
        currentLoco.apply {
            number = numberLocoState.text
            series = seriesLocoState.text
            type = pagerState == 1
            timeStartOfAcceptance = acceptedTimeState.value.startAccepted.time
            timeEndOfAcceptance = acceptedTimeState.value.endAccepted.time
            timeStartOfDelivery = deliveryTimeState.value.startDelivered.time
            timeEndOfDelivery = deliveryTimeState.value.endDelivered.time
            sectionList = if (this.type) {
                val list = mutableListOf<SectionElectric>()
                electricSectionListState.forEach { state ->
                    list.add(
                        SectionElectric(
                            id = state.sectionId,
                            acceptedEnergy = state.accepted.data?.toDoubleOrNull(),
                            deliveryEnergy = state.delivery.data?.toDoubleOrNull(),
                            acceptedRecovery = state.recoveryAccepted.data?.toDoubleOrNull(),
                            deliveryRecovery = state.recoveryDelivery.data?.toDoubleOrNull()
                        )
                    )
                }
                list
            } else {
                val list = mutableListOf<SectionDiesel>()
                dieselSectionListState.forEach { state ->
                    list.add(
                        SectionDiesel(
                            id = state.sectionId,
                            acceptedEnergy = state.accepted.data?.toDoubleOrNull(),
                            deliveryEnergy = state.delivery.data?.toDoubleOrNull(),
                            coefficient = state.coefficient.data?.toDoubleOrNull(),
                            fuelSupply = state.refuel.data?.toDoubleOrNull(),
                        )
                    )
                }
                list
            }
        }

        try {
            stateLocomotive.addOrReplace(currentLoco)
            clearField()
        } catch (e: Throwable) {
            Log.e("ERROR_ADDING_LOCO", e.message.toString())
        }
    }

    private suspend fun getTypeLoco(): Boolean =
        withContext(viewModelScope.coroutineContext) {
            dataStoreRepository.getTypeLoco().first()
        }

    private var defaultLocoType by mutableStateOf(true)

    init {
        viewModelScope.launch {
            defaultLocoType = getTypeLoco()
        }
    }

    private var currentLoco: Locomotive by Delegates.observable(
        initialValue = Locomotive(
            type = defaultLocoType
        )
    ) { _, _, loco ->
        setSeriesLoco(TextFieldValue(loco.series ?: ""))
        setNumberLoco(TextFieldValue(loco.number ?: ""))
        pagerState = if (loco.type) 1 else 0
        setTimeAccepted(loco)
        setTimeDelivery(loco)
        setSectionData(loco)
    }

    private fun setTimeAccepted(loco: Locomotive) {
        acceptedTimeState.value = acceptedTimeState.value.copy(
            startAccepted = AcceptedTimeState(
                time = loco.timeStartOfAcceptance,
                type = AcceptedType.START
            ),
            endAccepted = AcceptedTimeState(
                time = loco.timeEndOfAcceptance,
                type = AcceptedType.END
            ),
            formValid = true,
        )
    }

    private fun setTimeDelivery(loco: Locomotive) {
        deliveryTimeState.value = deliveryTimeState.value.copy(
            startDelivered = DeliveredTimeState(
                time = loco.timeStartOfDelivery,
                type = DeliveredType.START
            ),
            endDelivered = DeliveredTimeState(
                time = loco.timeEndOfDelivery,
                type = DeliveredType.END
            ),
            formValid = true,
        )
    }

    private var workTimeState: State<WorkTimeEditState> =
        mutableStateOf(WorkTimeEditState(formValid = true))

    var seriesLocoState by mutableStateOf(
        TextFieldValue(
            currentLoco.series ?: ""
        )
    )
        private set

    fun setSeriesLoco(newValue: TextFieldValue) {
        seriesLocoState = newValue
    }

    var numberLocoState by mutableStateOf(TextFieldValue(currentLoco.number ?: ""))
        private set

    fun setNumberLoco(newValue: TextFieldValue) {
        numberLocoState = newValue
    }

    var pagerState by mutableStateOf(if (defaultLocoType) 1 else 0)
        private set

    var acceptedTimeState = mutableStateOf(
        AcceptedBlockState(
            startAccepted = AcceptedTimeState(
                time = currentLoco.timeStartOfAcceptance,
                type = AcceptedType.START
            ),
            endAccepted = AcceptedTimeState(
                time = currentLoco.timeEndOfAcceptance,
                AcceptedType.END
            ),
            formValid = true
        )
    )
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
                    workTimeState.value.startTime.time?.let { timeStart ->
                        if (input < timeStart) {
                            val timeStartText = SimpleDateFormat(
                                DateAndTimeFormat.TIME_FORMAT, Locale.getDefault()
                            ).format(timeStart)
                            acceptedTimeState.value.errorMessage =
                                "Время приемки раньше явки $timeStartText"
                            return false
                        }
                    }
                    workTimeState.value.endTime.time?.let { endTime ->
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
                    workTimeState.value.startTime.time?.let { timeStart ->
                        if (input < timeStart) {
                            val timeStartText = SimpleDateFormat(
                                DateAndTimeFormat.TIME_FORMAT, Locale.getDefault()
                            ).format(timeStart)
                            acceptedTimeState.value.errorMessage =
                                "Время приемки раньше явки $timeStartText"
                            return false
                        }
                    }
                    workTimeState.value.endTime.time?.let { endTime ->
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

    var deliveryTimeState = mutableStateOf(
        DeliveryBlockState(
            startDelivered = DeliveredTimeState(
                time = currentLoco.timeStartOfDelivery,
                type = DeliveredType.START
            ),
            endDelivered = DeliveredTimeState(
                time = currentLoco.timeEndOfDelivery,
                type = DeliveredType.END
            ),
            formValid = true
        )
    )
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
                    workTimeState.value.startTime.time?.let { timeStart ->
                        if (input < timeStart) {
                            val timeStartText = SimpleDateFormat(
                                DateAndTimeFormat.TIME_FORMAT, Locale.getDefault()
                            ).format(timeStart)
                            deliveryTimeState.value.errorMessage =
                                "Время сдачи раньше явки $timeStartText"
                            return false
                        }
                    }
                    workTimeState.value.endTime.time?.let { timeEnd ->
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
                    workTimeState.value.startTime.time?.let { timeStart ->
                        if (input < timeStart) {
                            val timeStartText = SimpleDateFormat(
                                DateAndTimeFormat.TIME_FORMAT, Locale.getDefault()
                            ).format(timeStart)
                            deliveryTimeState.value.errorMessage =
                                "Время сдачи раньше явки $timeStartText"
                            return false
                        }
                    }
                    workTimeState.value.endTime.time?.let { timeEnd ->
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

    private fun setSectionData(locomotive: Locomotive) {
        locomotive.sectionList.forEach { section ->
            when (section) {
                is SectionDiesel -> {
                    dieselSectionListState.add(
                        SectionType.DieselSectionFormState(
                            sectionId = section.id,
                            accepted = DieselSectionFieldState(
                                data = section.acceptedEnergy?.str() ?: "",
                                type = DieselSectionType.ACCEPTED
                            ),
                            delivery = DieselSectionFieldState(
                                data = section.deliveryEnergy?.str() ?: "",
                                type = DieselSectionType.DELIVERY
                            ),
                            coefficient = DieselSectionFieldState(
                                data = section.coefficient?.str() ?: "",
                                type = DieselSectionType.COEFFICIENT
                            ),
                            refuel = DieselSectionFieldState(
                                data = section.fuelSupply?.str() ?: "",
                                type = DieselSectionType.REFUEL
                            ),
                            formValid = true,
                        )
                    )
                }
                is SectionElectric -> {
                    electricSectionListState.add(
                        SectionType.ElectricSectionFormState(
                            sectionId = section.id,
                            accepted = ElectricSectionFieldState(
                                data = section.acceptedEnergy?.str() ?: "",
                                type = ElectricSectionType.ACCEPTED
                            ),
                            delivery = ElectricSectionFieldState(
                                data = section.deliveryEnergy?.str() ?: "",
                                type = ElectricSectionType.DELIVERY
                            ),
                            recoveryAccepted = ElectricSectionFieldState(
                                data = section.acceptedRecovery?.str() ?: "",
                                type = ElectricSectionType.RECOVERY_ACCEPTED
                            ),
                            recoveryDelivery = ElectricSectionFieldState(
                                data = section.deliveryRecovery?.str() ?: "",
                                type = ElectricSectionType.RECOVERY_DELIVERY
                            ),
                            formValid = true,
                        )
                    )
                }
            }
        }
    }

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
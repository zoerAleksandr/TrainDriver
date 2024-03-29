package com.example.traindriver.ui.screen.adding_screen

import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.traindriver.data.repository.DataStoreRepository
import com.example.traindriver.data.util.ResultState
import com.example.traindriver.domain.entity.*
import com.example.traindriver.domain.use_case.AddRouteUseCase
import com.example.traindriver.domain.use_case.GetRouteByIdUseCase
import com.example.traindriver.ui.screen.ROUTE
import com.example.traindriver.ui.screen.adding_screen.state_holder.*
import com.example.traindriver.ui.util.collection_util.extension.addOrReplace
import com.example.traindriver.ui.util.long_util.minus
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.properties.Delegates

class AddingViewModel : ViewModel(), KoinComponent {
    private val dataStoreRepository: DataStoreRepository by inject()
    private val getRouteByIdUseCase: GetRouteByIdUseCase by inject()
    private val addRouteUseCase: AddRouteUseCase by inject()

    var timeEditState = mutableStateOf(WorkTimeEditState(formValid = true))
        private set

    var savesState by mutableStateOf<ResultState<Boolean>>(ResultState.Success(false))

    private var currentRoute: Route by Delegates.observable(
        initialValue = Route()
    ) { _, _, route ->
        setNumber(TextFieldValue(route.number ?: ""))
        setTimeEditState(route)
        setLocoListState(route)
        setTrainListState(route)
        setPassengerListState(route)
        setNotesState(route)
    }

    fun addRouteInRepository() {
        currentRoute.apply {
            number = numberRouteState.text
            timeStartWork = timeEditState.value.startTime.time
            timeEndWork = timeEditState.value.endTime.time
            notes = stateNotes.value
            locoList.clear()
            trainList.clear()
            passengerList.clear()
        }
        stateLocoList.forEach {
            currentRoute.locoList.addOrReplace(it)
        }
        stateTrainList.forEach {
            currentRoute.trainList.addOrReplace(it)
        }
        statePassengerList.forEach {
            currentRoute.passengerList.addOrReplace(it)
        }
        viewModelScope.launch {
            addRouteUseCase.execute(currentRoute).collect { result ->
                savesState = result
            }
        }
    }

    fun clearField() {
        currentRoute = currentRoute.copy(
            number = null,
            timeStartWork = null,
            timeEndWork = null,
            locoList = mutableListOf(),
            trainList = mutableListOf(),
            stationList = mutableListOf(),
            passengerList = mutableListOf()
        )
        stateLocoList.clear()
        stateTrainList.clear()
        statePassengerList.clear()
    }

    private fun setTimeEditState(route: Route) {
        timeEditState.value = timeEditState.value.copy(
            startTime = WorkTimeState(
                time = route.timeStartWork,
                type = WorkTimeType.START
            ),
            endTime = WorkTimeState(
                time = route.timeEndWork,
                type = WorkTimeType.END
            )
        )
    }

    fun setData(uid: String) {
/**
 * Проверка на то, содержится в uid константа ROUTE,
 * если да, значит uid не был передан в качестве uid для редактирования маршрута,
 * и нужно открыть экран с пустыми или ранее заполнеными полями*/
        if (!uid.contains(ROUTE.toRegex())) {
            viewModelScope.launch {
                getRouteByIdUseCase.execute(uid).collect {
                    when (it) {
                        is ResultState.Loading -> {
                            // TODO
                        }
                        is ResultState.Success -> {
                            it.data?.let { route ->
                                currentRoute = route
                                Log.d("ZZZ", "after setting = ${currentRoute.hashCode()}")
                            }
                        }
                        is ResultState.Failure -> {
                            // TODO
                        }
                    }
                }
            }
        }
    }

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
    private fun setLocoListState(route: Route) {
        stateLocoList.clear()
        route.locoList.forEach { locomotive ->
            stateLocoList.add(locomotive)
        }
    }
    fun deleteLocomotiveInRoute(
        locomotive: Locomotive
    ) {
        if (stateLocoList.contains(locomotive)) {
            stateLocoList.remove(locomotive)
        }
    }

    var stateTrainList = mutableStateListOf<Train>()
    private fun setTrainListState(route: Route) {
        stateTrainList.clear()
        route.trainList.forEach { train ->
            stateTrainList.add(train)
        }
    }
    fun deleteTrainInRoute(train: Train){
        if (stateTrainList.contains(train)){
            stateTrainList.remove(train)
        }
    }

    var statePassengerList = mutableStateListOf<Passenger>()

    private fun setPassengerListState(route: Route) {
        statePassengerList.clear()
        route.passengerList.forEach { passenger ->
            statePassengerList.add(passenger)
        }
    }
    fun deletePassengerInRoute(passenger: Passenger){
        if (statePassengerList.contains(passenger)){
            statePassengerList.remove(passenger)
        }
    }

    var stateNotes : MutableState<Notes?> = mutableStateOf(null)
    private fun setNotesState(route: Route){
        stateNotes.value = route.notes
    }

    var minTimeRest by mutableStateOf(0L)
        private set

    fun createEvent(event: WorkTimeEvent) {
        onEvent(event)
    }

    private fun onEvent(event: WorkTimeEvent) {
        when (event) {
            is WorkTimeEvent.EnteredStartTime -> {
                timeEditState.value = timeEditState.value.copy(
                    startTime = timeEditState.value.startTime.copy(
                        time = event.value
                    )
                )
            }
            is WorkTimeEvent.EnteredEndTime -> {
                timeEditState.value = timeEditState.value.copy(
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
                        timeEditState.value = timeEditState.value.copy(
                            formValid = timeValid
                        )
                    }
                    WorkTimeType.END -> {
                        val timeValid =
                            validateInput(timeEditState.value.endTime.time, WorkTimeType.END)
                        timeEditState.value = timeEditState.value.copy(
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
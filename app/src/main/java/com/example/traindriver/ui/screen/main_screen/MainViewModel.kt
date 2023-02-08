package com.example.traindriver.ui.screen.main_screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.traindriver.data.repository.DataRepository
import com.example.traindriver.data.repository.DataStoreRepository
import com.example.traindriver.data.util.ResultState
import com.example.traindriver.domain.entity.Itinerary
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*

typealias RouteListByMonthResponse = ResultState<List<Itinerary>>
typealias RouteResponse = ResultState<Itinerary>

class MainViewModel : ViewModel(), KoinComponent {
    private val dataStore: DataStoreRepository by inject()
    private val repository: DataRepository by inject()

    var listRoute by mutableStateOf<RouteListByMonthResponse>(ResultState.Loading())
        private set

    private fun getListItineraryByMonth(month: Int) {
        viewModelScope.launch {
            repository.getListItineraryByMonth(month).collect { result ->
                listRoute = result
            }
        }
    }

    init {
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH)
        getListItineraryByMonth(currentMonth)
    }
}
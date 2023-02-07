package com.example.traindriver.ui.screen.main_screen.elements

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.traindriver.domain.entity.Itinerary
import com.example.traindriver.domain.entity.Locomotive
import com.example.traindriver.domain.entity.Station
import com.example.traindriver.domain.entity.Train
import com.example.traindriver.ui.element_screen.HandleBottomSheet
import com.example.traindriver.ui.util.currentTimeInLong

@Composable
fun BottomSheetContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.88f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HandleBottomSheet()
        LazyColumn(modifier = Modifier.padding(top = 32.dp)) {
            items(20) {
                ItemMainScreen(
                    Itinerary(
                        timeEndWork = currentTimeInLong() + 10000000,
                        stationList = mutableListOf(
                            Station(stationName = "Луга"),
                            Station(stationName = "Санкт-Петербург Сортировочный Московский")
                        ),
                        trainList = mutableListOf(
                            Train(number = "2220")
                        ),
                        locoList = mutableListOf(
//                            Locomotive(series = "3эс4к", number = "064"),
                            Locomotive(series = "2эс4к", number = "164")
                        )
                    )
                ) { Log.d("ZZZ", "onClick itinerary") }
            }
            //TODO items
        }
    }
}
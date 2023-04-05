package com.example.traindriver.domain.use_case

import com.example.traindriver.data.util.ResultState
import com.example.traindriver.domain.entity.Locomotive
import com.example.traindriver.domain.repository.DataRepository
import kotlinx.coroutines.flow.Flow

class AddLocomotiveInRouteUseCase(val repository: DataRepository) {
    fun execute(locomotive: Locomotive): Flow<ResultState<Boolean>> =
        repository.addLocomotiveInRoute(locomotive)
}
package com.example.traindriver.domain.use_case

import com.example.traindriver.data.util.ResultState
import com.example.traindriver.domain.entity.Route
import com.example.traindriver.domain.repository.DataRepository
import kotlinx.coroutines.flow.Flow

class AddRouteUseCase(val repository: DataRepository) {
    fun execute(route: Route): Flow<ResultState<Boolean>> =
        repository.addRoute(route)
}
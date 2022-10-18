package com.example.traindriver.domain.use_case

import com.example.traindriver.data.retrofit.locale.LocationRetrofitClient
import com.example.traindriver.domain.entity.LocationResponse

class GetLocaleUseCase(private val retrofitClient: LocationRetrofitClient) {
    suspend fun execute(): LocationResponse {
        return  retrofitClient.getLocation()
    }
}
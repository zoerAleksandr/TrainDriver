package com.example.traindriver.data.retrofit.locale

import com.example.traindriver.domain.entity.LocationResponse
import retrofit2.http.GET

interface LocationApi {
    @GET("json")
    suspend fun getLocationAsync(): LocationResponse
}
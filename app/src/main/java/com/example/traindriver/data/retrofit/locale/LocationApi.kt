package com.example.traindriver.data.retrofit.locale

import com.example.traindriver.domain.entity.LocationResponse
import kotlinx.coroutines.Deferred
import retrofit2.http.GET

interface LocationApi {
    @GET("json")
    fun getLocationAsync(): Deferred<LocationResponse>
}
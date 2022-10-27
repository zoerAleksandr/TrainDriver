package com.example.traindriver.domain.use_case

import com.example.traindriver.data.retrofit.locale.LocationRetrofitClient
import com.example.traindriver.domain.entity.LocationResponse
import com.example.traindriver.ui.util.LocaleState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class GetLocaleUseCase(private val retrofitClient: LocationRetrofitClient) {
   fun execute(): Flow<LocaleState> {
        return retrofitClient.getLocation()
            .catch {
                emit(LocationResponse("OK", "OTHER"))
            }
            .map { response ->
                when (response.countryCode) {
                    "RU" -> {
                        LocaleState.RU
                    }
                    "BY" -> {
                        LocaleState.BY
                    }
                    "KZ" -> {
                        LocaleState.KZ
                    }
                    else -> {
                        LocaleState.OTHER
                    }
                }
            }
    }
}
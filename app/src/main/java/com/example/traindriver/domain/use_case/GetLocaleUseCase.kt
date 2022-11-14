package com.example.traindriver.domain.use_case

import com.example.traindriver.data.retrofit.locale.LocationRetrofitClient
import com.example.traindriver.domain.entity.LocationResponse
import com.example.traindriver.ui.util.LocaleUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class GetLocaleUseCase(private val retrofitClient: LocationRetrofitClient) {
    fun execute(): Flow<LocaleUser> {
        return retrofitClient.getLocation()
            .catch {
                emit(LocationResponse("FAILED", "OTHER"))
            }
            .map { response ->
                when (response.countryCode) {
                    "RU" -> {
                        LocaleUser.RU
                    }
                    "BY" -> {
                        LocaleUser.BY
                    }
                    "KZ" -> {
                        LocaleUser.KZ
                    }
                    else -> {
                        LocaleUser.OTHER
                    }
                }
            }
    }
}
package com.example.traindriver.data.retrofit.locale

import com.example.traindriver.BuildConfig
import com.example.traindriver.domain.entity.LocationResponse
import com.google.gson.GsonBuilder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_URL = "http://ip-api.com/"

class LocationRetrofitClient {

    fun getLocation(): Flow<LocationResponse> = flow {
        emit(createRetrofit().create(LocationApi::class.java).getLocationAsync())
    }

    private fun createRetrofit(): Retrofit {
        return if (BuildConfig.DEBUG) {
            createRetrofitWithLog()
        } else {
            return createRetrofitWithOutLog()
        }
    }

    private fun createRetrofitWithLog(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder().setLenient().create()
                )
            )
            .client(createClient())
            .build()
    }

    private fun createRetrofitWithOutLog(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder().setLenient().create()
                )
            )
            .build()
    }

    private fun createClient(): OkHttpClient {
        val loggingInterceptor =
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(loggingInterceptor)
        return httpClient.build()
    }
}
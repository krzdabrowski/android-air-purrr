package com.example.trubul.airpurrr.retrofit

import com.example.trubul.airpurrr.model.ApiModel
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header

interface ApiService {

    @GET("/v2/measurements/point?lat=52.16198&lng=21.02762")
    fun getApiDataAsync(@Header("apikey") apikey: String): Deferred<Response<ApiModel>>

    companion object {
        private const val BASE_URL = "https://airapi.airly.eu"

        fun create(): ApiService {
            return Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(MoshiConverterFactory.create())
                    .addCallAdapterFactory(CoroutineCallAdapterFactory())
                    .build()
                    .create(ApiService::class.java)
        }
    }
}
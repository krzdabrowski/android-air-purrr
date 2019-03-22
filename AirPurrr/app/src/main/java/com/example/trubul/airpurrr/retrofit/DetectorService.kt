package com.example.trubul.airpurrr.retrofit

import com.example.trubul.airpurrr.model.Detector
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

interface DetectorService {

    @GET("/data.json")
    fun getDetectorDataAsync(): Deferred<Response<Detector.Result>>

    companion object {
        private const val BASE_URL = "http://airpurrr.ga"

        fun create(): DetectorService {
            return Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(MoshiConverterFactory.create())
                    .addCallAdapterFactory(CoroutineCallAdapterFactory())
                    .build()
                    .create(DetectorService::class.java)
        }
    }
}
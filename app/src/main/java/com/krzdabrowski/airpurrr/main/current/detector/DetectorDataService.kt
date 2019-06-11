package com.krzdabrowski.airpurrr.main.current.detector

import com.krzdabrowski.airpurrr.BuildConfig
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

interface DetectorDataService {

    @GET("data.json")
    fun getDetectorDataAsync(): Deferred<Response<DetectorModel>>

    companion object {
        fun create(client: OkHttpClient): DetectorDataService {
            return Retrofit.Builder()
                    .client(client)
                    .baseUrl(BuildConfig.DETECTOR_HTTP_URL_MOCK)
                    .addConverterFactory(MoshiConverterFactory.create())
                    .addCallAdapterFactory(CoroutineCallAdapterFactory())
                    .build()
                    .create(DetectorDataService::class.java)
        }
    }
}
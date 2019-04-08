package com.example.trubul.airpurrr.retrofit

import com.example.trubul.airpurrr.model.DetectorModel
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface DetectorDataService {

    @GET("/data.json")
    fun getDetectorDataAsync(): Deferred<Response<DetectorModel>>

    companion object {
        private const val BASE_URL_HTTP = "http://airpurrr.ga"

        private val client = OkHttpClient.Builder()
                .connectTimeout(3, TimeUnit.SECONDS)
                .readTimeout(3, TimeUnit.SECONDS)
                .build()

        fun create(): DetectorDataService {
            return Retrofit.Builder()
                    .client(client)
                    .baseUrl(BASE_URL_HTTP)
                    .addConverterFactory(MoshiConverterFactory.create())
                    .addCallAdapterFactory(CoroutineCallAdapterFactory())
                    .build()
                    .create(DetectorDataService::class.java)
        }
    }
}
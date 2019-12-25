package com.krzdabrowski.airpurrr.main.current.detector

import com.krzdabrowski.airpurrr.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

interface DetectorDataService {

    @GET("static/data.json")
    suspend fun getDetectorDataAsync(): Response<DetectorModel>

    companion object {
        fun create(client: OkHttpClient): DetectorDataService {
            return Retrofit.Builder()
                    .client(client)
                    .baseUrl(BuildConfig.BASE_DETECTOR_URL)
                    .addConverterFactory(MoshiConverterFactory.create())
                    .build()
                    .create(DetectorDataService::class.java)
        }
    }
}
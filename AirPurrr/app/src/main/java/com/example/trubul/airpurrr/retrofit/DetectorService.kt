package com.example.trubul.airpurrr.retrofit

import com.example.trubul.airpurrr.model.Detector
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

interface DetectorService {

    @GET("/data.json")
    fun getDetectorData(): Call<Detector.Result>

    companion object {
        fun create(): DetectorService {
            val retrofit = Retrofit.Builder()
                    .baseUrl("http://airpurrr.ga")
                    .addConverterFactory(MoshiConverterFactory.create())
                    .build()

            return retrofit.create(DetectorService::class.java)
        }
    }
}
package com.krzdabrowski.airpurrr.main.current.detector

import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

interface DetectorControlService {

    @FormUrlEncoded
    @POST("login")
    suspend fun controlFanAsync(@Header("Authorization") authorization: String, @Field("shouldTurnOn") requestShouldTurnOn: String): ResponseBody

    companion object {
        private const val BASE_DETECTOR_HTTPS_URL = "https://airpurrr.eu/"

        fun create(client: OkHttpClient): DetectorControlService {
            return Retrofit.Builder()
                    .client(client)
                    .baseUrl(BASE_DETECTOR_HTTPS_URL)
                    .build()
                    .create(DetectorControlService::class.java)
        }
    }
}
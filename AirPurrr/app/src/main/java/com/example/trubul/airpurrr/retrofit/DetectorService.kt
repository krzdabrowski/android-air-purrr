package com.example.trubul.airpurrr.retrofit

import com.example.trubul.airpurrr.model.Detector
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface DetectorService {

    @GET("/data.json")
    fun getDetectorDataAsync(): Deferred<Response<Detector>>

    @FormUrlEncoded
    @POST("/login")
    fun controlFanAsync(@Header("Authorization") authorization: String, @Field("req") requestKey: String): Deferred<ResponseBody>

    companion object {
        private const val BASE_URL_HTTP = "http://airpurrr.ga"
        private const val BASE_URL_HTTPS = "https://airpurrr.ga"

        private val client = OkHttpClient.Builder()
                .connectTimeout(3, TimeUnit.SECONDS)
                .readTimeout(3, TimeUnit.SECONDS)
                .build()

        fun createHttp(): DetectorService {
            return Retrofit.Builder()
                    .client(client)
                    .baseUrl(BASE_URL_HTTP)
                    .addConverterFactory(MoshiConverterFactory.create())
                    .addCallAdapterFactory(CoroutineCallAdapterFactory())
                    .build()
                    .create(DetectorService::class.java)
        }

        fun createHttps(): DetectorService {
            return Retrofit.Builder()
                    .client(client)
                    .baseUrl(BASE_URL_HTTPS)
                    .addCallAdapterFactory(CoroutineCallAdapterFactory())
                    .build()
                    .create(DetectorService::class.java)
        }
    }
}
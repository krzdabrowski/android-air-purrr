package com.example.trubul.airpurrr.retrofit

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

interface DetectorControlService {

    @FormUrlEncoded
    @POST("/login")
    fun controlFanAsync(@Header("Authorization") authorization: String, @Field("req") requestKey: String): Deferred<ResponseBody>

    companion object {
        private const val BASE_URL_HTTPS = "https://airpurrr.ga"

        private val client = OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .build()

        fun create(): DetectorControlService {
            return Retrofit.Builder()
                    .client(DetectorControlService.client)
                    .baseUrl(BASE_URL_HTTPS)
                    .addCallAdapterFactory(CoroutineCallAdapterFactory())
                    .build()
                    .create(DetectorControlService::class.java)
        }
    }
}
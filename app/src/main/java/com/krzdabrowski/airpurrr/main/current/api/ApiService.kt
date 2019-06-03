package com.krzdabrowski.airpurrr.main.current.api

import com.krzdabrowski.airpurrr.BuildConfig
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface ApiService {

    @GET("v2/measurements/point")
    fun getApiDataAsync(@Header("apikey") apikey: String, @Query("lat") userLat: Double, @Query("lng") userLon: Double): Deferred<Response<ApiModel>>

    companion object {
        fun create(client: OkHttpClient): ApiService {
            return Retrofit.Builder()
                    .client(client)
                    .baseUrl(BuildConfig.BASE_API_URL)
                    .addConverterFactory(MoshiConverterFactory.create())
                    .addCallAdapterFactory(CoroutineCallAdapterFactory())
                    .build()
                    .create(ApiService::class.java)
        }
    }
}
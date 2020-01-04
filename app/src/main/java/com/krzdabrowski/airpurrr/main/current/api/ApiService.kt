package com.krzdabrowski.airpurrr.main.current.api

import com.krzdabrowski.airpurrr.BuildConfig
import kotlinx.coroutines.flow.Flow
import me.sianaki.flowretrofitadapter.FlowCallAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface ApiService {

    @GET("v2/measurements/point")
    fun getApiDataAsync(@Header("apikey") apikey: String, @Query("lat") userLat: Double, @Query("lng") userLon: Double): Flow<Response<ApiModel>>

    companion object {
        fun create(client: OkHttpClient): ApiService {
            return Retrofit.Builder()
                    .client(client)
                    .baseUrl(BuildConfig.BASE_API_URL)
                    .addCallAdapterFactory(FlowCallAdapterFactory.create())
                    .addConverterFactory(MoshiConverterFactory.create())
                    .build()
                    .create(ApiService::class.java)
        }
    }
}
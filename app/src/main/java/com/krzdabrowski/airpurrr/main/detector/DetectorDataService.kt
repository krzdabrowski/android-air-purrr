package com.krzdabrowski.airpurrr.main.detector

import com.krzdabrowski.airpurrr.BuildConfig
import kotlinx.coroutines.flow.Flow
import me.sianaki.flowretrofitadapter.FlowCallAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

interface DetectorDataService {

    @GET("static/data.json")
    fun getDetectorDataAsync(): Flow<Response<DetectorCurrentModel>>

    companion object {
        fun create(client: OkHttpClient): DetectorDataService {
            return Retrofit.Builder()
                    .client(client)
                    .baseUrl(BuildConfig.BASE_DETECTOR_URL)
                    .addCallAdapterFactory(FlowCallAdapterFactory.create())
                    .addConverterFactory(MoshiConverterFactory.create())
                    .build()
                    .create(DetectorDataService::class.java)
        }
    }
}
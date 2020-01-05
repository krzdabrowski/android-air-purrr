package com.krzdabrowski.airpurrr.main.detector

import com.krzdabrowski.airpurrr.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

interface DetectorControlService {

    @FormUrlEncoded
    @POST("control-on-off")
    suspend fun controlTurningFanOnOffAsync(@Field("onOff") key: String): ResponseBody

    @FormUrlEncoded
    @POST("control-high-low")
    suspend fun controlFanHighLowModeAsync(@Field("highLow") key: String): ResponseBody

    companion object {
        fun create(client: OkHttpClient): DetectorControlService {
            return Retrofit.Builder()
                    .client(client)
                    .baseUrl(BuildConfig.BASE_DETECTOR_URL)
                    .build()
                    .create(DetectorControlService::class.java)
        }
    }
}
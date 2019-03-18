package com.example.trubul.airpurrr.retrofit

import com.example.trubul.airpurrr.model.Api
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

interface ApiService {

    @GET("/pjp-api/rest/data/getData/3731")  // PM2.5
    fun getApiPm25Data(): Call<Api.Result>

    @GET("/pjp-api/rest/data/getData/3730")  // PM10
    fun getApiPm10Data(): Call<Api.Result>

    companion object {
        fun create(): ApiService {
            val retrofit = Retrofit.Builder()
                    .baseUrl("http://api.gios.gov.pl")
                    .addConverterFactory(MoshiConverterFactory.create())
                    .build()

            return retrofit.create(ApiService::class.java)
        }
    }
}
package com.example.trubul.airpurrr.retrofit

import com.example.trubul.airpurrr.model.Api
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

interface ApiService {

    @GET("/pjp-api/rest/data/getData/3731")
    fun getApiPm25DataAsync(): Deferred<Response<Api.Result>>

    @GET("/pjp-api/rest/data/getData/3730")
    fun getApiPm10DataAsync(): Deferred<Response<Api.Result>>

    companion object {
        private const val BASE_URL = "http://api.gios.gov.pl"

        fun create(): ApiService {
            return Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(MoshiConverterFactory.create())
                    .addCallAdapterFactory(CoroutineCallAdapterFactory())
                    .build()
                    .create(ApiService::class.java)
        }
    }
}
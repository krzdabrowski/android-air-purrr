package com.krzdabrowski.airpurrr.main.current.api

import android.location.Location
import com.krzdabrowski.airpurrr.BuildConfig
import retrofit2.HttpException
import timber.log.Timber

class ApiRepository(private val service: ApiService) {
    suspend fun fetchData(userLocation: Location): ApiModel? {
        try {
            val response = service.getApiDataAsync(BuildConfig.ApiKey, userLocation.latitude, userLocation.longitude)
            if (response.isSuccessful && response.body() != null) {
                return ApiAirlyConverter.getData(response)
            } else {
                Timber.e("ApiModel error: ${response.code()}")
            }
        } catch (e: HttpException) {
            Timber.e("API error: $e")
        } catch (e: Throwable) {
            Timber.e("API error: $e")
        }
        return null
    }
}
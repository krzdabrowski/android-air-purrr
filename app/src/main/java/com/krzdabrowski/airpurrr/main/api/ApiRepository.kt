package com.krzdabrowski.airpurrr.main.api

import android.location.Location
import com.krzdabrowski.airpurrr.BuildConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import timber.log.Timber

const val PERIODIC_DATA_REFRESH_INTERVAL = 1000 * 60 * 10L  // 10 minutes

class ApiRepository(private val service: ApiService) {
    fun fetchDataFlow(userLocation: Location): Flow<Pair<ApiCurrentModel, ApiForecastModel>> = flow {
        while (true) {
            try {
                service.getApiDataAsync(BuildConfig.ApiKey, userLocation.latitude, userLocation.longitude).collect { response ->
                    if (response.isSuccessful && response.body() != null) {
                        emit(ApiAirlyConverter.getData(response))
                    } else {
                        Timber.e("ApiModel error: ${response.code()}")
                    }
                }
            } catch (e: Throwable) {
                Timber.e("API data error: ${e.message}")
            } finally {
                delay(PERIODIC_DATA_REFRESH_INTERVAL)
            }
        }
    }
}
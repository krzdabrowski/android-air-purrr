package com.krzdabrowski.airpurrr.main.api

import android.location.Location
import com.krzdabrowski.airpurrr.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber

private const val PERIODIC_DATA_REFRESH_INTERVAL = 1000 * 60 * 15L  // 15 minutes

@ExperimentalCoroutinesApi
class ApiRepository(private val service: ApiService) {
    fun fetchDataFlow(userLocation: Location): Flow<Pair<ApiCurrentModel, ApiForecastModel>> = flow {
        while (true) {
            try {
                val response = service.getApiDataAsync(BuildConfig.ApiKey, userLocation.latitude, userLocation.longitude)

                if (response.isSuccessful && response.body() != null) {
                    emit(ApiAirlyConverter.getData(response))
                } else {
                    Timber.e("ApiModel error: ${response.code()}")
                }
            } catch (e: Throwable) {
                Timber.e("API data error: ${e.message}")
            } finally {
                delay(PERIODIC_DATA_REFRESH_INTERVAL)
            }
        }
    }.flowOn(Dispatchers.IO)
}
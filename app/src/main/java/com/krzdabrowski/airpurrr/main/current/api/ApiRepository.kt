package com.krzdabrowski.airpurrr.main.current.api

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.krzdabrowski.airpurrr.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import timber.log.Timber

class ApiRepository(private val service: ApiService) {

    fun fetchData(userLocation: Location): LiveData<ApiModel> {
        val result = MutableLiveData<ApiModel>()

        CoroutineScope(Dispatchers.IO).launch {
            val request = service.getApiDataAsync(BuildConfig.ApiKey, userLocation.latitude, userLocation.longitude)
            withContext(Dispatchers.Main) {
                try {
                    val response = request.await()
                    if (response.isSuccessful && response.body() != null) {
                        result.value = ApiAirlyConverter.getData(response)
                    } else {
                        Timber.e("ApiModel error: ${response.code()}")
                    }
                } catch (e: HttpException) {
                    Timber.e("API error: $e")
                } catch (e: Throwable) {
                    Timber.e("API error: $e")
                }
            }
        }
        return result
    }
}
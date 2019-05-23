package com.krzdabrowski.airpurrr.repository

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.krzdabrowski.airpurrr.BuildConfig
import com.krzdabrowski.airpurrr.model.ApiModel
import com.krzdabrowski.airpurrr.retrofit.ApiService
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
                        val currentValues = response.body()?.current?.values
                        if (currentValues?.get(1)?.get("name") == "PM25" && currentValues[2]?.get("name") == "PM10") {
                            val pm25 = currentValues[1]?.get("value") as Double
                            val pm10 = currentValues[2]?.get("value") as Double
                            result.value = ApiModel(null, doubleArrayOf(pm25, pm10))
                        }
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
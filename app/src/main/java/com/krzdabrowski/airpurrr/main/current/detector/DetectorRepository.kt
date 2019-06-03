package com.krzdabrowski.airpurrr.main.current.detector

import android.util.Base64
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import timber.log.Timber

class DetectorRepository(private val serviceHttp: DetectorDataService, private val serviceHttps: DetectorControlService) {

    fun fetchData(): LiveData<DetectorModel> {
        val result = MutableLiveData<DetectorModel>()

        CoroutineScope(Dispatchers.IO).launch {
            val request = serviceHttp.getDetectorDataAsync()
            withContext(Dispatchers.Main) {
                try {
                    val response = request.await()
                    if (response.isSuccessful && response.body() != null) {
                        result.value = response.body()
                    } else {
                        Timber.e("DetectorModel error: ${response.code()}")
                    }
                } catch (e: HttpException) {
                    Timber.d("HTTP error: ${e.message()}")
                } catch (e: Throwable) {
                    Timber.d("HTTP common error: ${e.message}")
                }
            }
        }
        return result
    }

    fun controlFan(turnOn: Boolean, hashedEmail: String, hashedPassword: String) {
        val auth = "Basic " + Base64.encodeToString("$hashedEmail:$hashedPassword".toByteArray(), Base64.NO_WRAP)

        CoroutineScope(Dispatchers.IO).launch {
            val request = if (turnOn) {
                serviceHttps.controlFanAsync(auth, "MANUAL=1")
            } else {
                serviceHttps.controlFanAsync(auth, "MANUAL=0")
            }
            withContext(Dispatchers.Main) {
                try {
                    request.await()
                } catch (e: HttpException) {
                    Timber.d("HTTPS error: ${e.message()}")
                } catch (e: Throwable) {
                    Timber.d("HTTPS common error: ${e.message}") }
            }
        }
    }
}
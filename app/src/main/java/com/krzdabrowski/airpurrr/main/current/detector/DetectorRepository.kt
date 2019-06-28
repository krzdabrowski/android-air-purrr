package com.krzdabrowski.airpurrr.main.current.detector

import android.util.Base64
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import timber.log.Timber

class DetectorRepository(private val serviceHttp: DetectorDataService, private val serviceHttps: DetectorControlService) {
    suspend fun fetchData(): DetectorModel? {
        try {
            val response = serviceHttp.getDetectorDataAsync()
            if (response.isSuccessful && response.body() != null) {
                return response.body()!!
            } else {
                Timber.e("DetectorModel error: ${response.code()}")
            }
        } catch (e: HttpException) {
            Timber.d("HTTP error: ${e.message()}")
        } catch (e: Throwable) {
            Timber.d("HTTP common error: ${e.message}")
        }
        return null
    }

    fun controlFan(turnOn: Boolean, hashedEmail: String, hashedPassword: String) {
        val auth = "Basic " + Base64.encodeToString("$hashedEmail:$hashedPassword".toByteArray(), Base64.NO_WRAP)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (turnOn) {
                    serviceHttps.controlFanAsync(auth, "MANUAL=1")
                } else {
                    serviceHttps.controlFanAsync(auth, "MANUAL=0")
                }
            } catch (e: HttpException) {
                Timber.d("HTTPS error: ${e.message()}")
            } catch (e: Throwable) {
                Timber.d("HTTPS common error: ${e.message}")
            }
        }
    }
}
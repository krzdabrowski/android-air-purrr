package com.krzdabrowski.airpurrr.main.current.detector

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

    fun controlFanOnOff(shouldTurnOn: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (shouldTurnOn) {
                    serviceHttps.controlTurningFanOnOffAsync("on")
                } else {
                    serviceHttps.controlTurningFanOnOffAsync("off")
                }
            } catch (e: HttpException) {
                Timber.d("HTTPS error: ${e.message()}")
            } catch (e: Throwable) {
                Timber.d("HTTPS common error: ${e.message}")
            }
        }
    }
    
    fun controlFanHighLow(shouldSwitchToHigh: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (shouldSwitchToHigh) {
                    serviceHttps.controlFanHighLowModeAsync("high")
                } else {
                    serviceHttps.controlFanHighLowModeAsync("low")
                }
            } catch (e: HttpException) {
                Timber.d("HTTPS error: ${e.message()}")
            } catch (e: Throwable) {
                Timber.d("HTTPS common error: ${e.message}")
            }
        }
    }
}
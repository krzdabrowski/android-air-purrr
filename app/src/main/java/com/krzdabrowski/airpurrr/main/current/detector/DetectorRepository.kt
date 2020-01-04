package com.krzdabrowski.airpurrr.main.current.detector

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import timber.log.Timber

const val PERIODIC_DATA_REFRESH_INTERVAL = 1000 * 60L  // 1 minute

class DetectorRepository(private val dataService: DetectorDataService, private val controlService: DetectorControlService) {
    fun fetchDataFlow(): Flow<DetectorModel?> = flow {
        while (true) {
            try {
                dataService.getDetectorDataAsync().collect { response ->
                    if (response.isSuccessful && response.body() != null) {
                        emit(response.body())
                    } else {
                        Timber.e("DetectorModel error: ${response.code()}")
                    }
                }
            } catch (e: Throwable) {
                Timber.e("DetectorRepository data error: ${e.message}")
            } finally {
                delay(PERIODIC_DATA_REFRESH_INTERVAL)
            }
        }
    }

    fun controlFanOnOff(shouldTurnOn: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (shouldTurnOn) {
                    controlService.controlTurningFanOnOffAsync("on")
                } else {
                    controlService.controlTurningFanOnOffAsync("off")
                }
            } catch (e: Throwable) {
                Timber.e("DetectorRepository onOff error: ${e.message}")
            }
        }
    }
    
    fun controlFanHighLow(shouldSwitchToHigh: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (shouldSwitchToHigh) {
                    controlService.controlFanHighLowModeAsync("high")
                } else {
                    controlService.controlFanHighLowModeAsync("low")
                }
            } catch (e: Throwable) {
                Timber.e("DetectorRepository highLow error: ${e.message}")
            }
        }
    }
}
package com.krzdabrowski.airpurrr.main.current.detector

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.lifecycle.liveData
import com.krzdabrowski.airpurrr.main.Conversion
import com.krzdabrowski.airpurrr.main.current.BaseViewModel
import kotlinx.coroutines.Dispatchers

class DetectorViewModel(private val repository: DetectorRepository) : BaseViewModel() {
    var data: DetectorModel? = null
    val autoModeSwitch = ObservableBoolean()
    val autoModeThreshold = ObservableInt()

    val purifierOnOffObservableState = ObservableBoolean()
    var purifierOnOffState = purifierOnOffObservableState.get()
    val purifierHighLowObservableState = ObservableBoolean()

    fun getLiveData() = liveData(Dispatchers.IO) {
        data = repository.fetchData()
        emit(data)
    }

    fun controlFanOnOff(shouldTurnOn: Boolean, login: String, password: String) {
        repository.controlFanOnOff(shouldTurnOn, login, password)
    }

    private fun controlFanHighLow(shouldSwitchToHigh: Boolean, login: String, password: String) {
        repository.controlFanHighLow(shouldSwitchToHigh, login, password)
    }

    fun checkAutoMode() {
        val isDataAvailable = data != null && data?.values != null
        if (!isDataAvailable) {
            return
        }

        val shouldTurnOn = !purifierOnOffState && autoModeSwitch.get()
                && (autoModeThreshold.get() < Conversion.pm25ToPercent(data?.values!!.pm25)
                || autoModeThreshold.get() < Conversion.pm10ToPercent(data?.values!!.pm10))
        val shouldTurnOff = purifierOnOffState && !autoModeSwitch.get()

        if (shouldTurnOn || shouldTurnOff) {
            purifierOnOffObservableState.set(!purifierOnOffState)
        }
    }

    fun checkPerformanceMode(shouldSwitchToHigh: Boolean, login: String, password: String) {
        if (purifierOnOffState) {
            controlFanHighLow(shouldSwitchToHigh, login, password)
        }
    }
}
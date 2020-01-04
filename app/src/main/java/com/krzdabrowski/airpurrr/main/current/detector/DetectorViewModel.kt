package com.krzdabrowski.airpurrr.main.current.detector

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.lifecycle.asLiveData
import com.krzdabrowski.airpurrr.main.Conversion
import com.krzdabrowski.airpurrr.main.current.BaseViewModel

class DetectorViewModel(private val repository: DetectorRepository) : BaseViewModel() {
    val autoModeSwitch = ObservableBoolean()
    val autoModeThreshold = ObservableInt()
    val purifierOnOffObservableState = ObservableBoolean()
    var purifierOnOffState = purifierOnOffObservableState.get()
    val purifierHighLowObservableState = ObservableBoolean()
    val liveData = repository.fetchDataFlow().asLiveData()

    fun controlFanOnOff(shouldTurnOn: Boolean) = repository.controlFanOnOff(shouldTurnOn)

    fun controlFanHighLow(shouldSwitchToHigh: Boolean) = repository.controlFanHighLow(shouldSwitchToHigh)

    fun checkAutoMode() {
        val data = liveData.value ?: return

        val shouldTurnOn = !purifierOnOffState && autoModeSwitch.get()
                && (autoModeThreshold.get() < Conversion.pm25ToPercent(data.values.pm25)
                || autoModeThreshold.get() < Conversion.pm10ToPercent(data.values.pm10))
        val shouldTurnOff = purifierOnOffState && !autoModeSwitch.get()

        if (shouldTurnOn || shouldTurnOff) {
            purifierOnOffObservableState.set(!purifierOnOffState)
        }
    }

    fun checkPerformanceMode(shouldSwitchToHigh: Boolean) {
        if (purifierOnOffState) {
            controlFanHighLow(shouldSwitchToHigh)
        }
    }
}
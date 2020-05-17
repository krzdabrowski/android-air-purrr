package com.krzdabrowski.airpurrr.main.detector

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.LiveData
import com.krzdabrowski.airpurrr.main.BaseViewModel
import com.krzdabrowski.airpurrr.main.helper.ConversionHelper

class DetectorViewModel(private val repository: DetectorRepository) : BaseViewModel() {
    val autoModeSwitch = ObservableBoolean()
    val autoModeThreshold = ObservableInt()
    val purifierOnOffObservableState = ObservableBoolean()
    var purifierOnOffState = purifierOnOffObservableState.get()
    val purifierHighLowObservableState = ObservableBoolean()
    val forecastPredictionType = ObservableField<ForecastPredictionType>()

    val currentSensorWorkstateLiveData: LiveData<String> = repository.currentSensorWorkstateLiveData
    val currentSensorAirPollutionValuesLiveData: LiveData<DetectorCurrentModel> = repository.currentSensorAirPollutionValuesLiveData
    val forecastValuesLiveData: LiveData<DetectorForecastModel> = repository.forecastValuesLiveData

    fun connectMqttClient() =
        repository.connectMqttClient(forecastPredictionType.get())

    fun controlAirPurifierFanState(shouldTurnOn: Boolean) =
        repository.publishAirPurifierFanState(shouldTurnOn)

    fun controlAirPurifierFanSpeed(shouldSwitchToHigh: Boolean) =
        repository.publishAirPurifierFanSpeed(shouldSwitchToHigh)

    fun sendSettingsAutomodeState(shouldAutomodeOn: Boolean) =
        repository.publishSettingsAutomodeState(shouldAutomodeOn)

    fun sendSettingsAutomodeThreshold(threshold: Int) =
        repository.publishSettingsAutomodeThreshold(threshold)

    fun sendSettingsPerformancemodeState(shouldPerformancemodeOn: Boolean) =
        repository.publishSettingsPerformancemodeState(shouldPerformancemodeOn)

    fun checkAutoMode() {
        val data = currentSensorAirPollutionValuesLiveData.value ?: return

        val shouldTurnOn = !purifierOnOffState && autoModeSwitch.get()
                && (autoModeThreshold.get() < ConversionHelper.pm25ToPercent(data.values.first)
                || autoModeThreshold.get() < ConversionHelper.pm10ToPercent(data.values.second))
        val shouldTurnOff = purifierOnOffState && !autoModeSwitch.get()

        if (shouldTurnOn || shouldTurnOff) {
            purifierOnOffObservableState.set(!purifierOnOffState)
        }
    }

    fun checkPerformanceMode(shouldSwitchToHigh: Boolean) {
        if (purifierOnOffState) {
            controlAirPurifierFanSpeed(shouldSwitchToHigh)
        }
    }

    fun subscribeToSelectedForecastType() {
        repository.subscribeToSelectedForecastType(forecastPredictionType.get())
    }
}
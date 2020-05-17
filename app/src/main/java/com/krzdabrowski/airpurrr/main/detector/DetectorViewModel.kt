package com.krzdabrowski.airpurrr.main.detector

import androidx.lifecycle.LiveData
import com.krzdabrowski.airpurrr.main.BaseViewModel

class DetectorViewModel(private val repository: DetectorRepository) : BaseViewModel() {
    val fanStateLiveData: LiveData<Boolean> = repository.fanStateLiveData
    val fanSpeedLiveData: LiveData<Boolean> = repository.fanSpeedLiveData
    val sensorWorkstateLiveData: LiveData<DetectorWorkstate> = repository.sensorWorkstateLiveData
    val sensorAirPollutionValuesLiveData: LiveData<DetectorCurrentModel> = repository.sensorAirPollutionValuesLiveData
    val forecastValuesLiveData: LiveData<DetectorForecastModel> = repository.forecastValuesLiveData

    fun connectMqttClient() =
        repository.connectMqttClient()

    fun controlAirPurifierFanState(shouldTurnOn: Boolean) =
        repository.publishAirPurifierFanState(shouldTurnOn)

    fun sendSettingsAutomodeState(shouldAutomodeOn: Boolean) =
        repository.publishSettingsAutomodeState(shouldAutomodeOn)

    fun sendSettingsAutomodeThreshold(threshold: Int) =
        repository.publishSettingsAutomodeThreshold(threshold)

    fun sendSettingsPerformancemodeState(shouldPerformancemodeOn: Boolean) =
        repository.publishSettingsPerformancemodeState(shouldPerformancemodeOn)

    fun subscribeToForecastLinearValues() =
        repository.subscribeToForecastLinearValues()

    fun subscribeToForecastNonlinearValues() =
        repository.subscribeToForecastNonlinearValues()

    fun subscribeToForecastXGBoostValues() =
        repository.subscribeToForecastXGBoostValues()

    fun subscribeToForecastNeuralNetworkValues() =
        repository.subscribeToForecastNeuralNetworkValues()
}
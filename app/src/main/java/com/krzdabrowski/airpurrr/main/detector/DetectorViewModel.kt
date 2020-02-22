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

    val currentValuesLiveData: LiveData<DetectorCurrentModel> = repository.currentValuesLiveData
    val forecastValuesLiveData: LiveData<DetectorForecastModel> = repository.forecastValuesLiveData
    val currentWorkstateLiveData: LiveData<String> = repository.currentWorkstateLiveData

    fun connectMqttClient() {
        repository.connectMqttClient()
    }

    fun controlFanOnOff(shouldTurnOn: Boolean) = repository.controlFanOnOff(shouldTurnOn)

    fun controlFanHighLow(shouldSwitchToHigh: Boolean) = repository.controlFanHighLow(shouldSwitchToHigh)

    fun checkAutoMode() {
        val data = currentValuesLiveData.value ?: return

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
            controlFanHighLow(shouldSwitchToHigh)
        }
    }

    fun getForecastPredictionData() {
        when (forecastPredictionType.get()) {
            ForecastPredictionType.LINEAR_REGRESSION -> repository.subscribeToForecastLinearRegressionValues()
            ForecastPredictionType.MACHINE_LEARNING -> repository.subscribeToForecastMachineLearningValues()
            ForecastPredictionType.NEURAL_NETWORK -> repository.subscribeToForecastNeuralNetworkValues()
        }
    }

    enum class ForecastPredictionType {
        LINEAR_REGRESSION,
        MACHINE_LEARNING,
        NEURAL_NETWORK
    }
}
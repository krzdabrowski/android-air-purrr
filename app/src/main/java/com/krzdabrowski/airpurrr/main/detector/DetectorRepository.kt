package com.krzdabrowski.airpurrr.main.detector

import androidx.lifecycle.MutableLiveData
import com.krzdabrowski.airpurrr.main.BaseForecastModel
import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.eclipse.paho.client.mqttv3.*
import timber.log.Timber
import java.nio.charset.StandardCharsets

class DetectorRepository(private val client: MqttAsyncClient, private val controlService: DetectorControlService) {
    internal val currentValuesLiveData = MutableLiveData<DetectorCurrentModel>()
    internal val forecastValuesLiveData = MutableLiveData<DetectorForecastModel>()
    internal val currentWorkstateLiveData = MutableLiveData<String>()
    private val forecastTopics = arrayOf("forecast/linear", "forecast/neuralnetwork", "forecast/xgboost")

    fun connectMqttClient(forecastPredictionType: ForecastPredictionType?) {
        if (client.isConnected) {
            return
        }

        val mqttOptions = MqttConnectOptions().apply {
            isAutomaticReconnect = true
            isCleanSession = false
        }

        try {
            client.connect(mqttOptions, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    subscribeToCurrentValues()
                    subscribeToCurrentWorkstate()

                    when (forecastPredictionType) {
                        ForecastPredictionType.LINEAR_REGRESSION -> subscribeToForecastLinearRegressionValues()
                        ForecastPredictionType.NEURAL_NETWORK -> subscribeToForecastNeuralNetworkValues()
                        ForecastPredictionType.XGBOOST -> subscribeToForecastXGBoostValues()
                    }
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Timber.e("DetectorRepository MQTT subscribe error: ${exception.toString()}")
                }
            })

        } catch (e: MqttException) {
            Timber.e("DetectorRepository MQTT error: ${e.message}")
        } catch (e: Throwable) {
            Timber.e("DetectorRepository data error: ${e.message}")
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

    private fun subscribeToCurrentValues() {
        client.subscribe("sds011/pollution", 0) { _, message ->
            val currentValues = message
                    ?.payload
                    ?.toString(StandardCharsets.UTF_8)
                    ?.split(',')
                    ?.map { it.toDouble() }

            if (!currentValues.isNullOrEmpty()) {
                Timber.d("MQTT pm25: ${currentValues[0]}, pm10: ${currentValues[1]}")
                currentValuesLiveData.postValue(DetectorCurrentModel(Pair(currentValues[0], currentValues[1])))
            }
        }
    }

    private fun subscribeToCurrentWorkstate() {
        client.subscribe("sds011/workstate", 0) { _, message ->
            val workstate = message
                    ?.payload
                    ?.toString(StandardCharsets.UTF_8)

            if (!workstate.isNullOrBlank()) {
                Timber.d("MQTT workstate: $workstate")
                currentWorkstateLiveData.postValue(workstate)
            }
        }
    }

    fun subscribeToForecastLinearRegressionValues() {
        client.unsubscribe(forecastTopics)
        client.subscribe(forecastTopics[0], 0) { _, message ->
            val forecastValues = message
                    ?.payload
                    ?.toString(StandardCharsets.UTF_8)

            val adapter = Moshi.Builder().build().adapter(BaseForecastModel.Result::class.java)

            if (!forecastValues.isNullOrEmpty()) {
                val result = adapter.fromJson(forecastValues)
                if (result != null) {
                    forecastValuesLiveData.postValue(DetectorForecastModel(result))
                }
            }
        }
    }

    fun subscribeToForecastNeuralNetworkValues() {
        // TODO:
        // client.unsubscribePozostale
        // client.subscribe("forecast/linear", 0) {  }

        // forecastValuesLiveData.postValue(DetectorForecastModel(mockedValues))
    }

    fun subscribeToForecastXGBoostValues() {
        // TODO:
        // client.unsubscribePozostale
        // client.subscribe("forecast/linear", 0) {  }

        // forecastValuesLiveData.postValue(DetectorForecastModel(mockedValues))
    }
}
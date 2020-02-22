package com.krzdabrowski.airpurrr.main.detector

import androidx.lifecycle.MutableLiveData
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

    fun connectMqttClient() {
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
            val values = message
                    ?.payload
                    ?.toString(StandardCharsets.UTF_8)
                    ?.split(',')
                    ?.map { it.toDouble() }

            if (!values.isNullOrEmpty()) {
                Timber.d("MQTT pm25: ${values[0]}, pm10: ${values[1]}")
                currentValuesLiveData.postValue(DetectorCurrentModel(Pair(values[0], values[1])))
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
        // TODO: mock
        val mockedValues = listOf(
        "01:00" to Pair(10f, 20f),
        "02:00" to Pair(20f, 40f),
        "03:00" to Pair(30f, 60f),
        "04:00" to Pair(40f, 80f),
        "05:00" to Pair(50f, 100f),
        "06:00" to Pair(60f, 120f),
        "07:00" to Pair(70f, 140f),
        "08:00" to Pair(80f, 160f)
        )

        // client.unsubscribePozostale
        // client.subscribe("forecast/linear", 0) {  }

        forecastValuesLiveData.postValue(DetectorForecastModel(mockedValues))
    }

    fun subscribeToForecastMachineLearningValues() {
        // TODO: mock
        val mockedValues = listOf(
                "01:00" to Pair(10f, 20f),
                "02:00" to Pair(20f, 40f),
                "03:00" to Pair(30f, 60f),
                "04:00" to Pair(40f, 80f),
                "05:00" to Pair(30f, 60f),
                "06:00" to Pair(20f, 40f),
                "07:00" to Pair(10f, 20f),
                "08:00" to Pair(5f, 10f)
        )

        // client.unsubscribePozostale
        // client.subscribe("forecast/linear", 0) {  }

        forecastValuesLiveData.postValue(DetectorForecastModel(mockedValues))
    }

    fun subscribeToForecastNeuralNetworkValues() {
        // TODO: mock
        val mockedValues = listOf(
                "01:00" to Pair(40f, 80f),
                "02:00" to Pair(30f, 60f),
                "03:00" to Pair(20f, 40f),
                "04:00" to Pair(10f, 20f),
                "05:00" to Pair(20f, 40f),
                "06:00" to Pair(30f, 60f),
                "07:00" to Pair(40f, 80f),
                "08:00" to Pair(50f, 100f)
        )

        // client.unsubscribePozostale
        // client.subscribe("forecast/linear", 0) {  }

        forecastValuesLiveData.postValue(DetectorForecastModel(mockedValues))
    }
}
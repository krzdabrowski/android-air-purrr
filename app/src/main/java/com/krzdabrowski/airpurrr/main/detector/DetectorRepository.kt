package com.krzdabrowski.airpurrr.main.detector

import androidx.lifecycle.MutableLiveData
import com.krzdabrowski.airpurrr.main.BaseForecastModel
import com.squareup.moshi.Moshi
import org.eclipse.paho.client.mqttv3.*
import timber.log.Timber
import java.nio.charset.StandardCharsets

class DetectorRepository(private val client: MqttAsyncClient) {
    internal val currentSensorWorkstateLiveData = MutableLiveData<String>()
    internal val currentSensorAirPollutionValuesLiveData = MutableLiveData<DetectorCurrentModel>()
    internal val forecastValuesLiveData = MutableLiveData<DetectorForecastModel>()

    private val fanTopics = arrayOf("airpurifier/fan/state", "airpurifier/fan/speed")
    private val sensorTopics = arrayOf("airpurifier/sensor/state", "airpurifier/sensor/pollution")
    private val settingsTopics = arrayOf("android/automode/state", "android/automode/threshold", "android/performancemode/state")
    private val forecastTopics = arrayOf("backend/forecast/linear", "backend/forecast/nonlinear", "backend/forecast/xgboost", "backend/forecast/neuralnetwork")
    private val forecastMessageListener = IMqttMessageListener { _, message ->
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
                    subscribeToCurrentSensorWorkstate()
                    subscribeToCurrentSensorAirPollutionValues()
                    subscribeToSelectedForecastType(forecastPredictionType)
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

    // region Publish
    fun publishAirPurifierFanState(shouldTurnOn: Boolean) {
        try {
            if (shouldTurnOn) {
                client.publish(fanTopics[0], MqttMessage("on".toByteArray()))
            } else {
                client.publish(fanTopics[0], MqttMessage("off".toByteArray()))
            }
        } catch (e: MqttException) {
            Timber.e("DetectorRepository publish fan state MqttException: ${e.message}")
        }  catch (e: Throwable) {
            Timber.e("DetectorRepository publish fan state error: ${e.message}")
        }
    }
    
    fun publishAirPurifierFanSpeed(shouldSwitchToHigh: Boolean) {
        try {
            if (shouldSwitchToHigh) {
                client.publish(fanTopics[1], MqttMessage("high".toByteArray()))
            } else {
                client.publish(fanTopics[1], MqttMessage("low".toByteArray()))
            }
        } catch (e: MqttException) {
            Timber.e("DetectorRepository publish fan speed MqttException: ${e.message}")
        }  catch (e: Throwable) {
            Timber.e("DetectorRepository publish fan speed error: ${e.message}")
        }
    }

    fun publishSettingsAutomodeState(shouldAutomodeOn: Boolean) {
        try {
            if (shouldAutomodeOn) {
                client.publish(settingsTopics[0], MqttMessage("on".toByteArray()))
            } else {
                client.publish(settingsTopics[0], MqttMessage("off".toByteArray()))
            }
        } catch (e: MqttException) {
            Timber.e("DetectorRepository publish settings automode MqttException: ${e.message}")
        }  catch (e: Throwable) {
            Timber.e("DetectorRepository publish settings automode error: ${e.message}")
        }
    }

    fun publishSettingsAutomodeThreshold(threshold: Int) {
        try {
            client.publish(settingsTopics[1], MqttMessage(threshold.toString().toByteArray()))
        } catch (e: MqttException) {
            Timber.e("DetectorRepository publish settings automode MqttException: ${e.message}")
        }  catch (e: Throwable) {
            Timber.e("DetectorRepository publish settings automode error: ${e.message}")
        }
    }

    fun publishSettingsPerformancemodeState(shouldPerformancemodeOn: Boolean) {
        try {
            if (shouldPerformancemodeOn) {
                client.publish(settingsTopics[2], MqttMessage("on".toByteArray()))
            } else {
                client.publish(settingsTopics[2], MqttMessage("off".toByteArray()))
            }
        } catch (e: MqttException) {
            Timber.e("DetectorRepository publish settings performancemode MqttException: ${e.message}")
        }  catch (e: Throwable) {
            Timber.e("DetectorRepository publish settings performancemode error: ${e.message}")
        }
    }
    // endregion

    // region Subscribe
    private fun subscribeToCurrentSensorWorkstate() {
        client.subscribe(sensorTopics[0], 0) { _, message ->
            val workstate = message
                    ?.payload
                    ?.toString(StandardCharsets.UTF_8)

            if (!workstate.isNullOrBlank()) {
                Timber.d("MQTT workstate: $workstate")
                currentSensorWorkstateLiveData.postValue(workstate)
            }
        }
    }

    private fun subscribeToCurrentSensorAirPollutionValues() {
        client.subscribe(sensorTopics[1], 0) { _, message ->
            val currentValues = message
                    ?.payload
                    ?.toString(StandardCharsets.UTF_8)
                    ?.split(',')
                    ?.map { it.toDouble() }

            if (!currentValues.isNullOrEmpty()) {
                Timber.d("MQTT pm25: ${currentValues[0]}, pm10: ${currentValues[1]}")
                currentSensorAirPollutionValuesLiveData.postValue(DetectorCurrentModel(Pair(currentValues[0], currentValues[1])))
            }
        }
    }

    fun subscribeToSelectedForecastType(forecastPredictionType: ForecastPredictionType?) {
        client.unsubscribe(forecastTopics)

        when (forecastPredictionType) {
            ForecastPredictionType.LINEAR -> subscribeToForecastLinearValues()
            ForecastPredictionType.NONLINEAR -> subscribeToForecastNonlinearValues()
            ForecastPredictionType.XGBOOST -> subscribeToForecastXGBoostValues()
            ForecastPredictionType.NEURAL_NETWORK -> subscribeToForecastNeuralNetworkValues()
        }
    }

    private fun subscribeToForecastLinearValues() =
        client.subscribe(forecastTopics[0], 0, forecastMessageListener)

    private fun subscribeToForecastNonlinearValues() =
        client.subscribe(forecastTopics[1], 0, forecastMessageListener)

    private fun subscribeToForecastXGBoostValues() =
        client.subscribe(forecastTopics[2], 0, forecastMessageListener)

    private fun subscribeToForecastNeuralNetworkValues() =
        client.subscribe(forecastTopics[3], 0, forecastMessageListener)
    // endregion
}
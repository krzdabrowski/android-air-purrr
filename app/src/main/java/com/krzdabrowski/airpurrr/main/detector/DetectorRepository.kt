package com.krzdabrowski.airpurrr.main.detector

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.eclipse.paho.client.mqttv3.*
import timber.log.Timber
import java.nio.charset.StandardCharsets

class DetectorRepository(private val client: MqttAsyncClient, private val controlService: DetectorControlService) {
    fun fetchData(): LiveData<DetectorCurrentModel> {
        val result = MutableLiveData<DetectorCurrentModel>()

        try {
            if (!client.isConnected) {
                val mqttOptions = MqttConnectOptions().apply {
                    isAutomaticReconnect = true
                    isCleanSession = false
                }

                client.connect(mqttOptions, null, object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        client.subscribe("sds011/workstate", 0) { _, message ->
                            val data = message
                                    ?.payload
                                    ?.toString(StandardCharsets.UTF_8)

                            Timber.d("MQTT workstate is: $data")
                        }

                        client.subscribe("sds011/pollution", 0) { _, message ->
                            val data = message
                                    ?.payload
                                    ?.toString(StandardCharsets.UTF_8)
                                    ?.split(',')
                                    ?.map { it.toDouble() }

                            if (data != null) {
                                result.postValue(DetectorCurrentModel("", DetectorCurrentModel.Data(data[0], data[1])))
                            }
                        }
                    }

                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                        Timber.e("DetectorRepository MQTT subscribe error: ${exception.toString()}")
                    }
                })
            }
        } catch (e: MqttException) {
            Timber.e("DetectorRepository MQTT error: ${e.message}")
        } catch (e: Throwable) {
            Timber.e("DetectorRepository data error: ${e.message}")
        }

        return result
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

    fun disconnectMqttClient() {
        if (client.isConnected) {
            client.disconnect()
        }
    }
}
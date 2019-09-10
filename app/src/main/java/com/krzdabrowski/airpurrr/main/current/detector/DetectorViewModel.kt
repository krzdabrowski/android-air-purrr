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
    val purifierObservableState = ObservableBoolean()
    var purifierState = purifierObservableState.get()

    fun getLiveData() = liveData(Dispatchers.IO) {
        data = repository.fetchData()
        emit(data)
    }

    fun controlFan(turnOn: Boolean, login: String, password: String) {
        repository.controlFan(turnOn, login, password)
    }

    fun checkAutoMode() {
        val isDataAvailable = data != null && data?.values != null
        if (!isDataAvailable) {
            return
        }

        val shouldTurnOn = !purifierState && autoModeSwitch.get()
                && (autoModeThreshold.get() < Conversion.pm25ToPercent(data?.values!!.pm25)
                || autoModeThreshold.get() < Conversion.pm10ToPercent(data?.values!!.pm10))
        val shouldTurnOff = purifierState && !autoModeSwitch.get()

        if (shouldTurnOn || shouldTurnOff) {
            purifierObservableState.set(!purifierState)
        }
    }
}
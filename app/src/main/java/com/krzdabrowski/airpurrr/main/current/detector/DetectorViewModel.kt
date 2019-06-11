package com.krzdabrowski.airpurrr.main.current.detector

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.lifecycle.LiveData
import com.krzdabrowski.airpurrr.main.current.BaseViewModel

class DetectorViewModel(private val repository: DetectorRepository) : BaseViewModel() {
    private lateinit var liveData: LiveData<DetectorModel>
    val autoModeSwitch = ObservableBoolean()
    val autoModeThreshold = ObservableInt()
    val purifierObservableState = ObservableBoolean()
    var purifierState = purifierObservableState.get()

    // possible refactor VMs to use LiveData coroutines with liveData block (until lifecycle v2.2.0 comes out of alpha)
    fun getLiveData(): LiveData<DetectorModel> {
        liveData = repository.fetchData()
        return liveData
    }

    fun controlFan(turnOn: Boolean, login: String, password: String) {
        repository.controlFan(turnOn, login, password)
    }

    fun checkAutoMode() {
        val isDataAvailable = ::liveData.isInitialized && liveData.value != null && liveData.value?.values != null
        if (!isDataAvailable) {
            return
        }

        val shouldTurnOn = !purifierState && autoModeSwitch.get() &&
                autoModeThreshold.get() < liveData.value?.values!!.pm25 || autoModeThreshold.get() < liveData.value?.values!!.pm10
        val shouldTurnOff = purifierState && !autoModeSwitch.get()

        if (shouldTurnOn) {
            purifierState = true
            purifierObservableState.set(purifierState)
        } else if (shouldTurnOff) {
            purifierState = false
            purifierObservableState.set(purifierState)
        }
    }
}
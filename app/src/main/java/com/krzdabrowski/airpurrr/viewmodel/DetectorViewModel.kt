package com.krzdabrowski.airpurrr.viewmodel

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.krzdabrowski.airpurrr.model.DetectorModel
import com.krzdabrowski.airpurrr.repository.DetectorRepository

class DetectorViewModel(private val repository: DetectorRepository) : ViewModel() {
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
        if (liveData.value != null && liveData.value?.values != null) {
            if (!purifierState && autoModeSwitch.get() &&
                    autoModeThreshold.get() < liveData.value?.values!!.pm25 || autoModeThreshold.get() < liveData.value?.values!!.pm10) {
                purifierState = true
                purifierObservableState.set(purifierState)
            } else if (purifierState && !autoModeSwitch.get()) {
                purifierState = false
                purifierObservableState.set(purifierState)
            }
        }
    }
}
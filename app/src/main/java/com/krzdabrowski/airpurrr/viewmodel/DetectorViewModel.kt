package com.krzdabrowski.airpurrr.viewmodel

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.krzdabrowski.airpurrr.model.DetectorModel
import com.krzdabrowski.airpurrr.repository.DetectorRepository

class DetectorViewModel(private val repository: DetectorRepository) : ViewModel() {
    private lateinit var liveData: LiveData<DetectorModel>
    val purifierState = ObservableBoolean()
    val autoModeSwitch = ObservableBoolean()
    val autoModeThreshold = ObservableInt()

    fun getLiveData(): LiveData<DetectorModel> {
        liveData = repository.fetchData()
        return liveData
    }

    fun controlFan(turnOn: Boolean, login: String, password: String) {
        repository.controlFan(turnOn, login, password)
    }

    fun checkAutoMode() {
        if (liveData.value != null && liveData.value?.values != null) {
            if (!purifierState.get() && autoModeSwitch.get() && autoModeThreshold.get() < liveData.value?.values!!.pm25 || autoModeThreshold.get() < liveData.value?.values!!.pm10) {
                purifierState.set(true)
            } else if (purifierState.get() && !autoModeSwitch.get()) {
                purifierState.set(false)
            }

            purifierState.notifyChange()
        }
    }
}
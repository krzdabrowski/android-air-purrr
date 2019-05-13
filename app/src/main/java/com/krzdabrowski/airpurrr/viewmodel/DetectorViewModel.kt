package com.krzdabrowski.airpurrr.viewmodel

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.krzdabrowski.airpurrr.model.DetectorModel
import com.krzdabrowski.airpurrr.repository.DetectorRepository

class DetectorViewModel(private val repository: DetectorRepository) : ViewModel() {
    private lateinit var liveData: LiveData<DetectorModel>
    val purifierState = ObservableBoolean(false)
    val autoModeSwitch = ObservableBoolean(false)
    val autoModeThreshold = ObservableInt(100)

    fun getLiveData(): LiveData<DetectorModel> {
        liveData = repository.fetchData()
        return liveData
    }

    fun controlFan(turnOn: Boolean, login: String, password: String) {
        repository.controlFan(turnOn, login, password)
    }

    fun checkAutoMode() {
        if (autoModeSwitch.get() && liveData.value != null && liveData.value?.values != null)
            if (autoModeThreshold.get() < liveData.value?.values!!.pm25 || autoModeThreshold.get() < liveData.value?.values!!.pm10) {
                purifierState.set(true)
                purifierState.notifyChange()
        }
    }
}
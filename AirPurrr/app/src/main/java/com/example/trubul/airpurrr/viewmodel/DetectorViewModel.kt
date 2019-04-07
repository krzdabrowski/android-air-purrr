package com.example.trubul.airpurrr.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.trubul.airpurrr.model.DetectorModel
import com.example.trubul.airpurrr.repository.DetectorRepository

class DetectorViewModel(private val repository: DetectorRepository) : ViewModel() {

    private lateinit var detectorLiveData: LiveData<DetectorModel>

    fun getLiveData(): LiveData<DetectorModel> {
        detectorLiveData = repository.fetchData()
        return detectorLiveData
    }

    fun controlFan(turnOn: Boolean, login: String, password: String) {
        repository.controlFan(turnOn, login, password)
    }
}
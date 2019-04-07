package com.example.trubul.airpurrr.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.trubul.airpurrr.model.Detector
import com.example.trubul.airpurrr.repository.DetectorRepository

class DetectorViewModel(private val repository: DetectorRepository) : ViewModel() {

    var detectorLiveData: LiveData<Detector> = MutableLiveData<Detector>()

    fun getData(): LiveData<Detector> {
        detectorLiveData = repository.fetchData()
        return detectorLiveData
    }
}
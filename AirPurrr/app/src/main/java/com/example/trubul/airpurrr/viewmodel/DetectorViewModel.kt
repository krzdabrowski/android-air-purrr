package com.example.trubul.airpurrr.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.trubul.airpurrr.model.DetectorModel
import com.example.trubul.airpurrr.repository.DetectorRepository

class DetectorViewModel(private val repository: DetectorRepository) : ViewModel() {

    var detectorLiveData: LiveData<DetectorModel> = MutableLiveData<DetectorModel>()

    fun getData(): LiveData<DetectorModel> {
        detectorLiveData = repository.fetchData()
        return detectorLiveData
    }
}
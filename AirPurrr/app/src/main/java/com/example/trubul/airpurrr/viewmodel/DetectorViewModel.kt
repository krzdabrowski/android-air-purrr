package com.example.trubul.airpurrr.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.trubul.airpurrr.model.Detector
import com.example.trubul.airpurrr.repository.DetectorRepository

class DetectorViewModel(private val repository: DetectorRepository) : ViewModel() {

    lateinit var detectorResult: LiveData<Detector.Result>

    fun getData(): LiveData<Detector.Result> {
        detectorResult = repository.fetchData()
        return detectorResult
    }
}
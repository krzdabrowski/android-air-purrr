package com.krzdabrowski.airpurrr.main

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.ViewModel

open class BaseViewModel : ViewModel() {
    val flagDetectorApi = ObservableBoolean()

    fun onDataClick() {
        flagDetectorApi.set(!flagDetectorApi.get())
    }
}
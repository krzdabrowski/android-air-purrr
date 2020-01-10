package com.krzdabrowski.airpurrr.main

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.ViewModel

open class BaseViewModel : ViewModel() {
    val flagDetectorApi = ObservableBoolean()
    lateinit var forecastClickCallback: OnForecastCallback

    fun onDataClick() {
        flagDetectorApi.set(!flagDetectorApi.get())
    }

    fun onForecastDataClick() {
        onDataClick()
        forecastClickCallback.onRefresh()
    }

    interface OnForecastCallback {
        fun onRefresh()
    }
}
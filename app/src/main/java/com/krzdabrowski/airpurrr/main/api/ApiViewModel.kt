package com.krzdabrowski.airpurrr.main.api

import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.krzdabrowski.airpurrr.main.BaseViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

private const val LATITUDE = 52.16194
private const val LONGITUDE = 21.02762

@ExperimentalCoroutinesApi
class ApiViewModel(repository: ApiRepository) : BaseViewModel() {
    var userLocation = MutableLiveData<Location>().apply { value = getDefaultLocation() }
    val liveData = repository.fetchDataFlow(userLocation.value!!).asLiveData()

    private fun getDefaultLocation(): Location {
        val defaultLocation = Location("")
        defaultLocation.latitude = LATITUDE
        defaultLocation.longitude = LONGITUDE
        return defaultLocation
    }
}
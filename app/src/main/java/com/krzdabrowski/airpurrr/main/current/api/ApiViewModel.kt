package com.krzdabrowski.airpurrr.main.current.api

import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.krzdabrowski.airpurrr.main.current.BaseViewModel
import com.krzdabrowski.airpurrr.main.current.api.ApiViewModel.GpsDefaultCoordinates.LATITUDE
import com.krzdabrowski.airpurrr.main.current.api.ApiViewModel.GpsDefaultCoordinates.LONGITUDE
import kotlinx.coroutines.Dispatchers

class ApiViewModel(private val repository: ApiRepository) : BaseViewModel() {
    var userLocation = MutableLiveData<Location>().apply { value = getDefaultLocation() }

    fun getLiveData() = liveData(Dispatchers.IO) {
        val data = repository.fetchData(userLocation.value!!)
        emit(data)
    }

    private fun getDefaultLocation(): Location {
        val defaultLocation = Location("")
        defaultLocation.latitude = LATITUDE
        defaultLocation.longitude = LONGITUDE
        return defaultLocation
    }

    object GpsDefaultCoordinates {
        const val LATITUDE = 52.16194
        const val LONGITUDE = 21.02762
    }
}
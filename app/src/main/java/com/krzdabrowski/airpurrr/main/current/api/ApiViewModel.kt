package com.krzdabrowski.airpurrr.main.current.api

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.krzdabrowski.airpurrr.main.current.api.ApiViewModel.GpsDefaultCoordinates.LATITUDE
import com.krzdabrowski.airpurrr.main.current.api.ApiViewModel.GpsDefaultCoordinates.LONGITUDE

class ApiViewModel(private val repository: ApiRepository) : ViewModel() {

    private lateinit var liveData: LiveData<ApiModel>
    var userLocation = MutableLiveData<Location>().apply { value = getDefaultLocation() }

    fun getLiveData(): LiveData<ApiModel> {
        liveData = repository.fetchData(userLocation.value!!)
        return liveData
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
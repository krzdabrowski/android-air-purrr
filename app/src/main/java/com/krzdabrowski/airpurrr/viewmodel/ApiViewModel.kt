package com.krzdabrowski.airpurrr.viewmodel

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.krzdabrowski.airpurrr.helper.GPS_DEFAULT_LATITUDE
import com.krzdabrowski.airpurrr.helper.GPS_DEFAULT_LONGITUDE
import com.krzdabrowski.airpurrr.model.ApiModel
import com.krzdabrowski.airpurrr.repository.ApiRepository

class ApiViewModel(private val repository: ApiRepository) : ViewModel() {

    private lateinit var liveData: LiveData<ApiModel>
    var userLocation = MutableLiveData<Location>().apply { value = getDefaultLocation() }

    fun getLiveData(): LiveData<ApiModel> {
        liveData = repository.fetchData(userLocation.value!!)
        return liveData
    }

    private fun getDefaultLocation(): Location {
        val defaultLocation = Location("")
        defaultLocation.latitude = GPS_DEFAULT_LATITUDE
        defaultLocation.longitude = GPS_DEFAULT_LONGITUDE
        return defaultLocation
    }
}
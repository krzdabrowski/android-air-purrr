package com.krzdabrowski.airpurrr.viewmodel

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.krzdabrowski.airpurrr.model.ApiModel
import com.krzdabrowski.airpurrr.repository.ApiRepository
import com.krzdabrowski.airpurrr.utils.*
import java.util.concurrent.TimeUnit

class ApiViewModel(private val repository: ApiRepository) : ViewModel() {
    private lateinit var liveData: LiveData<ApiModel>
    var userLocation = MutableLiveData<Location>().apply { value = setDefaultLocation() }

    fun getLiveData(): LiveData<ApiModel> {
        liveData = repository.fetchData(userLocation.value!!)
        return liveData
    }

    private fun setDefaultLocation(): Location {
        val defaultLocation = Location("")
        defaultLocation.latitude = GPS_DEFAULT_LATITUDE
        defaultLocation.longitude = GPS_DEFAULT_LONGITUDE
        return defaultLocation
    }

    fun runPeriodicFetching() {
        val locationArray = doubleArrayOf(userLocation.value!!.latitude, userLocation.value!!.longitude)
        val locationData = workDataOf(WORKER_KEY_LOCATION_DATA to locationArray)

        val periodicWorkRequest = PeriodicWorkRequestBuilder<DataCurrentDownloadWorker>(15, TimeUnit.MINUTES).addTag(WORKER_TAG_API_PERIODIC_REQUEST)
                .setInputData(locationData)
                .build()
        WorkManager.getInstance().enqueueUniquePeriodicWork(WORKER_TAG_API_PERIODIC_REQUEST, ExistingPeriodicWorkPolicy.REPLACE, periodicWorkRequest)
    }
}
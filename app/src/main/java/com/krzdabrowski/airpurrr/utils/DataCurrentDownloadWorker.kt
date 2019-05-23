package com.krzdabrowski.airpurrr.utils

import android.content.Context
import android.location.Location
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.krzdabrowski.airpurrr.repository.ApiRepository
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber

class DataCurrentDownloadWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams), KoinComponent {
    override fun doWork(): Result {
        val apiRepository: ApiRepository by inject()
        apiRepository.fetchData(getLocationDataFromInput())

        return Result.success()
    }

    private fun getLocationDataFromInput(): Location {
        val input = inputData.getDoubleArray(WORKER_KEY_LOCATION_DATA)
        val location = Location("")

        location.latitude = input!![0]
        location.longitude = input[1]
        Timber.d("latitude is: ${location.latitude}, long is: ${location.longitude}")

        return location
    }
}
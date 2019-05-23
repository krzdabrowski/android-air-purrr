package com.krzdabrowski.airpurrr.utils

import android.content.Context
import android.location.Location
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.krzdabrowski.airpurrr.repository.ApiRepository
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber

class DataCurrentDownloadWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams), KoinComponent {
    override fun doWork(): Result {
        val apiRepository: ApiRepository by inject()

        val apiResult = apiRepository.fetchData(getLocationDataFromInput())
        val outputData = workDataOf(WORKER_KEY_API_RESULT to apiResult.value!!.data)

        return Result.success(outputData)
    }

    private fun getLocationDataFromInput(): Location {
        val input = inputData.getDoubleArray(WORKER_KEY_LOCATION_DATA)
        val location = Location("")

        location.latitude = input!![0] // TODO: null here
        location.longitude = input[1]
        Timber.d("latitude is: ${location.latitude}, long is: ${location.longitude}")

        return location
    }
}
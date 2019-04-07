package com.example.trubul.airpurrr.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.trubul.airpurrr.model.Detector
import com.example.trubul.airpurrr.retrofit.DetectorService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import timber.log.Timber

class DetectorRepository(private val service: DetectorService) {

    fun fetchData(): LiveData<Detector> {
        val result = MutableLiveData<Detector>()

        CoroutineScope(Dispatchers.IO).launch {
            val request = service.getDetectorDataAsync()
            withContext(Dispatchers.Main) {
                try {
                    val response = request.await()
                    if (response.isSuccessful && response.body() != null) {
                        result.value = response.body()
                        Timber.d("start value is: ${result.value}")
//                        binding.flagDetectorApi = false
//                        binding.detector = response.body()!!.values
                    } else {
                        Timber.e("Detector error: ${response.code()}")
                    }
                } catch (e: HttpException) {
                    Timber.e("Detector error: $e")
                } catch (e: Throwable) {
                    Timber.e("Detector error: $e")
                }
            }
        }
        return result
    }
}
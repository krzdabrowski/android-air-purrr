package com.example.trubul.airpurrr.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.trubul.airpurrr.model.DetectorModel
import com.example.trubul.airpurrr.retrofit.DetectorService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import timber.log.Timber

class DetectorRepository(private val service: DetectorService) {

    fun fetchData(): LiveData<DetectorModel> {
        val result = MutableLiveData<DetectorModel>()

        CoroutineScope(Dispatchers.IO).launch {
            val request = service.getDetectorDataAsync()
            withContext(Dispatchers.Main) {
                try {
                    val response = request.await()
                    if (response.isSuccessful && response.body() != null) {
                        result.value = response.body()
                    } else {
                        Timber.e("DetectorModel error: ${response.code()}")
                    }
                } catch (e: HttpException) {
                    Timber.e("DetectorModel error: $e")
                } catch (e: Throwable) {
                    Timber.e("DetectorModel error: $e")
                }
            }
        }
        return result
    }
}
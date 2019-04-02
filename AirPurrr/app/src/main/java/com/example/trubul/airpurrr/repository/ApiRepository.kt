package com.example.trubul.airpurrr.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.trubul.airpurrr.model.Api
import com.example.trubul.airpurrr.retrofit.ApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import timber.log.Timber

class ApiRepository(private val service: ApiService) {

    fun fetchData(): LiveData<Api.Result> {
        val result = MutableLiveData<Api.Result>()

        CoroutineScope(Dispatchers.IO).launch {
            val requestPm25 = service.getApiPm25DataAsync()
            val requestPm10 = service.getApiPm10DataAsync()
            withContext(Dispatchers.Main) {
                try {
                    val responsePm25 = requestPm25.await()
                    val responsePm10 = requestPm10.await()
                    if (responsePm25.isSuccessful && responsePm25.body() != null) {
                        for (i in responsePm25.body()!!.values.indices) {
                            if (responsePm25.body()!!.values[i].value != null) {
                                result.value?.values?.add(0, Api.Values(responsePm25.body()!!.values[i].value, responsePm25.body()!!.values[i].date))
                                break
                            } else continue
                        }
                    }

                    if (responsePm10.isSuccessful && responsePm10.body() != null) {
                        for (i in responsePm10.body()!!.values.indices) {
                            if (responsePm10.body()!!.values[i].value != null) {
                                result.value?.values?.add(1, Api.Values(responsePm10.body()!!.values[i].value, responsePm10.body()!!.values[i].date))
                                break
                            } else continue
                        }
                    }
                } catch (e: HttpException) {
                    Timber.e("API error: $e")
                } catch (e: Throwable) {
                    Timber.e("API error: $e")
                }
            }
        }

        return result
    }
}
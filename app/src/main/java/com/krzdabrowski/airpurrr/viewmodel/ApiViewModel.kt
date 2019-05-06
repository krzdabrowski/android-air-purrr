package com.krzdabrowski.airpurrr.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.krzdabrowski.airpurrr.model.ApiModel
import com.krzdabrowski.airpurrr.repository.ApiRepository

class ApiViewModel(private val repository: ApiRepository) : ViewModel() {

    private lateinit var apiLiveData: LiveData<ApiModel>

    fun getLiveData(): LiveData<ApiModel> {
        apiLiveData = repository.fetchData()
        return apiLiveData
    }
}
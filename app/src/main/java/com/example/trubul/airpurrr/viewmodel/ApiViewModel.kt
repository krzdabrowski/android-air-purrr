package com.example.trubul.airpurrr.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.trubul.airpurrr.model.ApiModel
import com.example.trubul.airpurrr.repository.ApiRepository

class ApiViewModel(private val repository: ApiRepository) : ViewModel() {

    private lateinit var apiLiveData: LiveData<ApiModel>

    fun getLiveData(): LiveData<ApiModel> {
        apiLiveData = repository.fetchData()
        return apiLiveData
    }
}
package com.example.trubul.airpurrr.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.trubul.airpurrr.model.ApiModel
import com.example.trubul.airpurrr.repository.ApiRepository

class ApiViewModel(private val repository: ApiRepository) : ViewModel() {

    lateinit var apiResult: LiveData<ApiModel>

    fun getData(): LiveData<ApiModel> {
        apiResult = repository.fetchData()
        return apiResult
    }

}
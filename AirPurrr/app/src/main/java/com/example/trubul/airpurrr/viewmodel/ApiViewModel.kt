package com.example.trubul.airpurrr.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.trubul.airpurrr.model.Api
import com.example.trubul.airpurrr.repository.ApiRepository

class ApiViewModel(private val repository: ApiRepository) : ViewModel() {

    lateinit var apiResult: LiveData<Api>

    fun getData(): LiveData<Api> {
        apiResult = repository.fetchData()
        return apiResult
    }

}
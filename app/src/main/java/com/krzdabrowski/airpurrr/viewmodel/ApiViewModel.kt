package com.krzdabrowski.airpurrr.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.krzdabrowski.airpurrr.model.ApiModel
import com.krzdabrowski.airpurrr.repository.ApiRepository

class ApiViewModel(private val repository: ApiRepository) : ViewModel() {

    private lateinit var liveData: LiveData<ApiModel>

    fun getLiveData(): LiveData<ApiModel> {
        liveData = repository.fetchData()
        return liveData
    }
}
package com.example.trubul.airpurrr.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {
    var email = MutableLiveData<String>()
    var password = MutableLiveData<String>()

}
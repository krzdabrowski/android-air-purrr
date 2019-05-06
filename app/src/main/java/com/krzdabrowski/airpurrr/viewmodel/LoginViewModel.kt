package com.krzdabrowski.airpurrr.viewmodel

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {
    var email = MutableLiveData<String>()
    var password = MutableLiveData<String>()
    var isFormValid = MutableLiveData<Boolean>()

    var isEmailError = ObservableBoolean()
    var isPasswordError = ObservableBoolean()

    fun isEmailValid(login: String) = isEmailError.set(login.isBlank())
    fun isPasswordValid(password: String) = isPasswordError.set(password.isBlank())

    fun onLoginButtonClick() {
        if (email.value == null || password.value == null || isEmailError.get() || isPasswordError.get()) {
            return
        }
        isFormValid.value = true
    }
}
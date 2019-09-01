package com.krzdabrowski.airpurrr.login

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val isFormValid = MutableLiveData<Boolean>(false)

    val isEmailError = ObservableBoolean()
    val isPasswordError = ObservableBoolean()
    val emailErrorType = ObservableField<String?>()
    val passwordErrorType = ObservableField<String?>()

    fun isEmailValid(login: String?) = isEmailError.set(login.isNullOrBlank())
    fun isPasswordValid(password: String?) = isPasswordError.set(password.isNullOrBlank())

    fun onLoginButtonClick() {
        isEmailValid(email.value)
        isPasswordValid(password.value)

        val isFormInvalid = email.value == null || password.value == null || isEmailError.get() || isPasswordError.get()
        if (isFormInvalid) {
            return
        }

        isFormValid.value = true
    }
}
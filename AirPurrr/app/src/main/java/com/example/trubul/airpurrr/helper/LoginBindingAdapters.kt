package com.example.trubul.airpurrr.helper

import androidx.databinding.BindingAdapter
import com.example.trubul.airpurrr.R
import com.google.android.material.textfield.TextInputEditText

@BindingAdapter("app:errorValidationEmail")
fun TextInputEditText.showEmailValidationError(isEmailError: Boolean) {
    setValidationError(isEmailError)
}

@BindingAdapter("app:errorValidationPassword")
fun TextInputEditText.showPasswordValidationError(isPasswordError: Boolean) {
    setValidationError(isPasswordError)
}

private fun TextInputEditText.setValidationError(errorField: Boolean) {
    if (errorField) error = context.getString(R.string.login_message_error_empty_field)
    else error = null
}
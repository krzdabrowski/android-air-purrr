package com.krzdabrowski.airpurrr.login

import androidx.databinding.BindingAdapter
import com.krzdabrowski.airpurrr.R
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
    if (errorField) error = context.getString(R.string.login_error_empty_field)
    else error = null
}
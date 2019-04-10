package com.example.trubul.airpurrr.helper

import androidx.databinding.BindingAdapter
import com.example.trubul.airpurrr.R
import com.google.android.material.textfield.TextInputEditText

@BindingAdapter("app:errorValidationEmail")
fun TextInputEditText.showEmailValidationError(isEmailError: Boolean) {
    if (isEmailError) {
        error = context.getString(R.string.login_message_error_empty_field)
    } else {
        error = null
    }
}

@BindingAdapter("app:errorValidationPassword")
fun TextInputEditText.showPasswordValidationError(isPasswordError: Boolean) {
    if (isPasswordError) {
        error = context.getString(R.string.login_message_error_empty_field)
    } else {
        error = null
    }
}
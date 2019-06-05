package com.krzdabrowski.airpurrr.login

import androidx.databinding.BindingAdapter
import com.krzdabrowski.airpurrr.R
import com.google.android.material.textfield.TextInputEditText

@BindingAdapter("app:errorValidation", "app:errorType")
fun TextInputEditText.showValidationError(errorField: Boolean, errorType: String?) {
    if (errorField) error = context.getString(R.string.login_error_empty_field)
    else error = null
}
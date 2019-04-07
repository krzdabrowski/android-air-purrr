package com.example.trubul.airpurrr

import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import com.example.trubul.airpurrr.helper.ConversionHelper
import com.example.trubul.airpurrr.model.Detector

@BindingAdapter(value = ["app:type", "app:dataPercentage"])
fun TextView.bindDataPercentage(type: String, data: Detector.Result?) {
    if (data?.values != null) {
        if (type == "PM2.5") {
            text = resources.getString(R.string.main_data_percentage, ConversionHelper.pm25ToPercent(data.values.pm25))
        } else if (type == "PM10") {
            text = resources.getString(R.string.main_data_percentage, ConversionHelper.pm10ToPercent(data.values.pm10))
        }
    } else {
        text = resources.getString(R.string.main_data_percentage, 0.0)
    }
}

@BindingAdapter(value = ["app:type", "app:dataUgm3"])
fun TextView.bindDataUgm3(type: String, data: Detector.Result?) {
    if (data?.values != null) {
        if (type == "PM2.5") {
            text = resources.getString(R.string.main_data_ugm3, data.values.pm25)
        } else if (type == "PM10") {
            text = resources.getString(R.string.main_data_ugm3, data.values.pm10)
        }
    } else {
        text = resources.getString(R.string.main_data_ugm3, 0.0)
    }
}

@BindingAdapter(value = ["app:type", "app:dataColor"])
fun ConstraintLayout.bindBackgroundColor(type: String, data: Detector.Result?) {
    val values = data?.values

    val valuePerc: Double = if (type == "PM2.5") {
        ConversionHelper.pm25ToPercent(values?.pm25)
    } else {
        ConversionHelper.pm10ToPercent(values?.pm10)
    }

    if (valuePerc == 0.0) {
        setBackgroundResource(R.drawable.data_unavailable)
    } else if (valuePerc > 0 && valuePerc <= 50) {
        setBackgroundResource(R.drawable.data_green)
    } else if (valuePerc > 50 && valuePerc <= 100) {
        setBackgroundResource(R.drawable.data_lime)
    } else if (valuePerc > 100 && valuePerc <= 200) {
        setBackgroundResource(R.drawable.data_yellow)
    } else {
        setBackgroundResource(R.drawable.data_red)
    }
}
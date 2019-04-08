package com.example.trubul.airpurrr.helper

import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import com.example.trubul.airpurrr.R
import com.example.trubul.airpurrr.model.ApiModel
import com.example.trubul.airpurrr.model.BaseModel
import com.example.trubul.airpurrr.model.DetectorModel

@BindingAdapter(value = ["app:source", "app:date"])
fun TextView.bindDate(source: Boolean, data: BaseModel?) {
    if (source) {
        if (data == null) {
            text = resources.getString(R.string.main_data_info_api_empty)
        } else if (data is ApiModel){
            text = resources.getString(R.string.main_data_info_api, data.values[0].date)
        }
    } else {
        text = resources.getString(R.string.main_data_info_indoors)
    }
}

@BindingAdapter(value = ["app:type", "app:dataPercentage"])
fun TextView.bindDataPercentage(type: String, data: BaseModel?) {
    text = resources.getString(R.string.main_data_percentage, 0.0)

    if (data is DetectorModel) {
        if (data.values != null) {
            if (type == "PM2.5") {
                text = resources.getString(R.string.main_data_percentage, ConversionHelper.pm25ToPercent(data.values.pm25))
            } else if (type == "PM10") {
                text = resources.getString(R.string.main_data_percentage, ConversionHelper.pm10ToPercent(data.values.pm10))
            }
        }
    } else if (data is ApiModel) {
        if (type == "PM2.5") {
            text = resources.getString(R.string.main_data_percentage, ConversionHelper.pm25ToPercent(data.values[0].value.toDouble()))
        } else if (type == "PM10") {
            text = resources.getString(R.string.main_data_percentage, ConversionHelper.pm10ToPercent(data.values[1].value.toDouble()))
        }
    }
}

@BindingAdapter(value = ["app:type", "app:dataUgm3"])
fun TextView.bindDataUgm3(type: String, data: BaseModel?) {
    text = resources.getString(R.string.main_data_ugm3, 0.0)

    if (data is DetectorModel) {
        if (data.values != null) {
            if (type == "PM2.5") {
                text = resources.getString(R.string.main_data_ugm3, data.values.pm25)
            } else if (type == "PM10") {
                text = resources.getString(R.string.main_data_ugm3, data.values.pm10)
            }
        }
    } else if (data is ApiModel) {
        if (type == "PM2.5") {
            text = resources.getString(R.string.main_data_ugm3, data.values[0].value.toDouble())
        } else if (type == "PM10") {
            text = resources.getString(R.string.main_data_ugm3, data.values[1].value.toDouble())
        }
    }
}

@BindingAdapter(value = ["app:type", "app:dataColor"])
fun ConstraintLayout.bindBackgroundColor(type: String, data: BaseModel?) {
    var valuePerc = 0.0

    if (data is DetectorModel) {
        val values = data.values
        if (type == "PM2.5") {
            valuePerc = ConversionHelper.pm25ToPercent(values?.pm25)
        } else if (type == "PM10"){
            valuePerc = ConversionHelper.pm10ToPercent(values?.pm10)
        }
    } else if (data is ApiModel) {
        val values = data.values
        if (type == "PM2.5") {
            valuePerc = ConversionHelper.pm25ToPercent(values[0].value.toDouble())
        } else if (type == "PM10") {
            valuePerc = ConversionHelper.pm10ToPercent(values[1].value.toDouble())
        }
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
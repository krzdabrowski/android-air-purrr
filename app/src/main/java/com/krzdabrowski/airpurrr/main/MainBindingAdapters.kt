package com.krzdabrowski.airpurrr.main

import android.widget.RelativeLayout
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.krzdabrowski.airpurrr.R
import com.krzdabrowski.airpurrr.common.Conversion
import com.krzdabrowski.airpurrr.main.current.api.ApiModel
import com.krzdabrowski.airpurrr.main.current.BaseModel
import com.krzdabrowski.airpurrr.main.current.detector.DetectorModel

@BindingAdapter(value = ["app:source", "app:model"])
fun bindSource(textView: TextView, source: Boolean, model: BaseModel?) {
    if (source) {
        if (model == null) {
            textView.text = textView.resources.getString(R.string.main_data_info_api_empty)
        } else if (model is ApiModel){
            textView.text = textView.resources.getString(R.string.main_data_info_api)
        }
    } else {
        textView.text = textView.resources.getString(R.string.main_data_info_indoors)
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
            text = resources.getString(R.string.main_data_ugm3, data.data.first)
        } else if (type == "PM10") {
            text = resources.getString(R.string.main_data_ugm3, data.data.second)
        }
    }
}

@BindingAdapter(value = ["app:type", "app:dataColor"])
fun RelativeLayout.bindBackgroundColor(type: String, data: BaseModel?) {
    var valuePerc = 0.0

    if (data is DetectorModel) {
        val values = data.values
        if (type == "PM2.5") {
            valuePerc = Conversion.pm25ToPercent(values?.pm25)
        } else if (type == "PM10"){
            valuePerc = Conversion.pm10ToPercent(values?.pm10)
        }
    } else if (data is ApiModel) {
        val values = data.data
        if (type == "PM2.5") {
            valuePerc = Conversion.pm25ToPercent(values.first)
        } else if (type == "PM10") {
            valuePerc = Conversion.pm10ToPercent(values.second)
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
package com.example.trubul.airpurrr.helper

import android.view.View
import androidx.databinding.BindingAdapter
import com.example.trubul.airpurrr.R

object ConversionHelper {

    @JvmStatic
    @BindingAdapter(value = ["dataType", "dataValue"])
    fun setBackgroundColor(view: View, type: String, value: Double) {
        val valuePerc: Double = if (type == "PM2.5") {
            pm25ToPercent(value)
        } else {
            pm10ToPercent(value)
        }

        if (valuePerc == 0.0) {
            view.setBackgroundResource(R.drawable.data_unavailable)
        } else if (valuePerc > 0 && valuePerc <= 50) {
            view.setBackgroundResource(R.drawable.data_green)
        } else if (valuePerc > 50 && valuePerc <= 100) {
            view.setBackgroundResource(R.drawable.data_lime)
        } else if (valuePerc > 100 && valuePerc <= 200) {
            view.setBackgroundResource(R.drawable.data_yellow)
        } else {
            view.setBackgroundResource(R.drawable.data_red)
        }
    }

    private fun pm25ToPercent(pm25: Double): Double {
        return 4 * pm25
    }

    private fun pm10ToPercent(pm10: Double): Double {
        return 2 * pm10
    }
}
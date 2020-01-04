package com.krzdabrowski.airpurrr.main.current.detector

import android.content.Context
import com.krzdabrowski.airpurrr.R
import com.krzdabrowski.airpurrr.main.Conversion
import com.krzdabrowski.airpurrr.main.current.BaseModel

data class DetectorModel(val workstate: String, val values: Values) : BaseModel() {
    data class Values(val pm25: Double, val pm10: Double)

    override fun getSource(context: Context): String {
        return context.getString(R.string.main_data_info_indoors)
    }

    override fun getDataPercentage(context: Context, type: String): String {
        return context.getString(R.string.main_data_percentage, getPercentageDouble(type))
    }

    override fun getDataUgm3(context: Context, type: String): String {
        return when (type) {
            "PM2.5" -> context.getString(R.string.main_data_ugm3, values.pm25)
            "PM10" -> context.getString(R.string.main_data_ugm3, values.pm10)
            else -> ""
        }
    }

    override fun getPercentageDouble(type: String): Double {
        return when (type) {
            "PM2.5" -> Conversion.pm25ToPercent(values.pm25)
            "PM10" -> Conversion.pm10ToPercent(values.pm10)
            else -> 0.0
        }
    }
}
package com.krzdabrowski.airpurrr.main.current.api

import android.content.Context
import com.krzdabrowski.airpurrr.R
import com.krzdabrowski.airpurrr.main.Conversion
import com.krzdabrowski.airpurrr.main.current.BaseModel

data class ApiModel(val current: Values?, @Transient val data: Pair<Double, Double>) : BaseModel() {
    data class Values(var values: MutableList<Map<String?, Any?>?>?)

    override fun getSource(context: Context): String {
        return context.getString(R.string.main_data_info_api) ?: context.getString(R.string.main_data_info_api_empty)
    }

    override fun getDataPercentage(context: Context, type: String): String {
        return context.getString(R.string.main_data_percentage, getPercentageDouble(type))
    }

    override fun getDataUgm3(context: Context, type: String): String {
        return when (type) {
            "PM2.5" -> context.getString(R.string.main_data_ugm3, data.first)
            "PM10" -> context.getString(R.string.main_data_ugm3, data.second)
            else -> ""
        }
    }

    override fun getPercentageDouble(type: String): Double {
        return when (type) {
            "PM2.5" -> Conversion.pm25ToPercent(data.first)
            "PM10" -> Conversion.pm10ToPercent(data.second)
            else -> 0.0
        }
    }
}
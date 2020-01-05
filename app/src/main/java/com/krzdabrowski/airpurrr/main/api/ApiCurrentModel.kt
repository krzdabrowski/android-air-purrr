package com.krzdabrowski.airpurrr.main.api

import android.content.Context
import com.krzdabrowski.airpurrr.R
import com.krzdabrowski.airpurrr.main.helper.ConversionHelper
import com.krzdabrowski.airpurrr.main.BaseModel

data class ApiCurrentModel(@Transient val result: Pair<Double, Double>) : BaseModel() {
    data class Data(val values: List<Map<String?, Any?>?>?)

    override fun getSource(context: Context): String {
        return context.getString(R.string.main_data_info_api) ?: context.getString(R.string.main_data_info_api_empty)
    }

    override fun getDataPercentage(context: Context, type: String): String {
        return context.getString(R.string.main_data_percentage, getPercentageDouble(type))
    }

    override fun getDataUgm3(context: Context, type: String): String {
        return when (type) {
            "PM2.5" -> context.getString(R.string.main_data_ugm3, result.first)
            "PM10" -> context.getString(R.string.main_data_ugm3, result.second)
            else -> ""
        }
    }

    override fun getPercentageDouble(type: String): Double {
        return when (type) {
            "PM2.5" -> ConversionHelper.pm25ToPercent(result.first)
            "PM10" -> ConversionHelper.pm10ToPercent(result.second)
            else -> 0.0
        }
    }
}
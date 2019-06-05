package com.krzdabrowski.airpurrr.main.current.api

import android.content.Context
import com.krzdabrowski.airpurrr.R
import com.krzdabrowski.airpurrr.common.Conversion
import com.krzdabrowski.airpurrr.main.current.BaseModel

data class ApiModel(val current: Values?, @Transient val data: Pair<Double, Double>) : BaseModel() {
    data class Values(var values: MutableList<Map<String?, Any?>?>?)

    override fun getDataPercentage(context: Context, type: String): String {
        if (type == "PM2.5") {
            return context.getString(R.string.main_data_percentage, Conversion.pm25ToPercent(data.first))
        } else if (type == "PM10") {
            return context.getString(R.string.main_data_percentage, Conversion.pm10ToPercent(data.second))
        }
        return ""
    }
}
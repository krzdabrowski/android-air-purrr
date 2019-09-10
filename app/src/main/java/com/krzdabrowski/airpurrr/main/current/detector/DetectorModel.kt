package com.krzdabrowski.airpurrr.main.current.detector

import android.content.Context
import com.krzdabrowski.airpurrr.R
import com.krzdabrowski.airpurrr.main.Conversion
import com.krzdabrowski.airpurrr.main.current.BaseModel

data class DetectorModel(val workstate: String, val values: Values?) : BaseModel() {
    data class Values(val pm25: Double, val pm10: Double)

    override fun getSource(context: Context): String {
        return context.getString(R.string.main_data_info_indoors)
    }

    override fun getDataPercentage(context: Context, type: String): String {
        if (values != null) {
            return context.getString(R.string.main_data_percentage, getPercentageDouble(type))
        }
        return ""
    }

    override fun getDataUgm3(context: Context, type: String): String {
        if (values != null) {
            if (type == "PM2.5") {
                return context.getString(R.string.main_data_ugm3, values.pm25)
            } else if (type == "PM10") {
                return context.getString(R.string.main_data_ugm3, values.pm10)
            }
        }
        return ""
    }

    override fun getPercentageDouble(type: String): Double {
        if (type == "PM2.5") {
            return Conversion.pm25ToPercent(values?.pm25)
        } else if (type == "PM10"){
            return Conversion.pm10ToPercent(values?.pm10)
        }
        return 0.0
    }
}
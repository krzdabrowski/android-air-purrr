package com.krzdabrowski.airpurrr.main.api

import com.squareup.moshi.Json

data class ApiForecastModel(@Transient val result: List<Pair<String, Pair<Double, Double>>>) {
    data class Data(@field:Json(name = "tillDateTime") val date: String?, val values: List<Map<String?, Any?>?>?)

//    override fun getSource(context: Context): String {
//        return context.getString(R.string.main_data_info_api) ?: context.getString(R.string.main_data_info_api_empty)
//    }
//
//    override fun getDataPercentage(context: Context, type: String): String {
//        return context.getString(R.string.main_data_percentage, getPercentageDouble(type))
//    }
//
//    override fun getDataUgm3(context: Context, type: String): String {
//        return when (type) {
//            "PM2.5" -> context.getString(R.string.main_data_ugm3, data.first)
//            "PM10" -> context.getString(R.string.main_data_ugm3, data.second)
//            else -> ""
//        }
//    }
//
//    override fun getPercentageDouble(type: String): Double {
//        return when (type) {
//            "PM2.5" -> ConversionHelper.pm25ToPercent(data.first)
//            "PM10" -> ConversionHelper.pm10ToPercent(data.second)
//            else -> 0.0
//        }
//    }
}
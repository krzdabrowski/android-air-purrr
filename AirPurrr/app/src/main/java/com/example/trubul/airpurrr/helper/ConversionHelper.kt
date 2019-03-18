package com.example.trubul.airpurrr.helper

object ConversionHelper {

    @JvmStatic
    fun pm25ToPercent(pm25: Double): Double {
        return 4 * pm25
    }

    @JvmStatic
    fun pm10ToPercent(pm10: Double): Double {
        return 2 * pm10
    }
}
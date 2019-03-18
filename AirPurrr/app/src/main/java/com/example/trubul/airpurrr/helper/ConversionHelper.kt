package com.example.trubul.airpurrr.helper

object ConversionHelper {

    @JvmStatic
    fun pm25ToPercent(pm25: Double): Double {
        val pm25Perc = 4 * pm25
        return pm25Perc
    }

    @JvmStatic
    fun pm10ToPercent(pm10: Double): Double {
        val pm10Perc = 2 * pm10
        return pm10Perc
    }

    @JvmStatic
    fun toPercent(pmData: MutableList<Double>): MutableList<Double> {
        val percData = mutableListOf<Double>()
        percData.add(pm25ToPercent(pmData[0]))
        percData.add(pm10ToPercent(pmData[1]))
        return percData
    }
}
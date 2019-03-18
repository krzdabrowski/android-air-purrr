package com.example.trubul.airpurrr.helper

object ConversionHelper {
    @JvmStatic
    fun toPercent(pmData: MutableList<Double>): MutableList<Double> {
        pmData[0] = 4 * pmData[0]  // PM2.5
        pmData[1] = 2 * pmData[1]  // PM10
        return pmData
    }
}
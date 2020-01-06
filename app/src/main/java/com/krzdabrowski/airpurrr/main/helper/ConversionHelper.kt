package com.krzdabrowski.airpurrr.main.helper

import java.text.SimpleDateFormat
import java.util.*

object ConversionHelper {
    fun pm25ToPercent(pm25: Double?): Double {
        return pm25?.times(4) ?: 0.0
    }

    fun pm10ToPercent(pm10: Double?): Double {
        return pm10?.times(2) ?: 0.0
    }

    fun formatDateToLocalTimezone(dateFromApi: String): String {
        val sdfUtc = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'", Locale.getDefault())
        sdfUtc.timeZone = TimeZone.getTimeZone("UTC")

        val sdfLocal = SimpleDateFormat("HH:mm", Locale.getDefault())
        sdfLocal.timeZone = TimeZone.getDefault()

        return sdfLocal.format(sdfUtc.parse(dateFromApi))
    }
}
package com.krzdabrowski.airpurrr.main

import android.content.Context
import androidx.core.content.res.ResourcesCompat
import com.krzdabrowski.airpurrr.R

abstract class BaseForecastModel {
    abstract val result: Result

    abstract fun getSource(context: Context): String

    fun getBackgroundColorInt(context: Context, dataPercentage: Double): Int {
        return when (dataPercentage) {
            0.0 -> ResourcesCompat.getColor(context.resources, R.color.color_main_primary, null)
            in 0.1..50.0 -> ResourcesCompat.getColor(context.resources, R.color.color_main_data_green, null)
            in 50.1..100.0 -> ResourcesCompat.getColor(context.resources, R.color.color_main_data_lime, null)
            in 100.1..200.0 -> ResourcesCompat.getColor(context.resources, R.color.color_main_data_yellow, null)
            else -> ResourcesCompat.getColor(context.resources, R.color.color_main_data_red, null)
        }
    }

    data class Result(
        val hours: MutableList<String>,
        val pm25: MutableList<Float>,
        val pm10: MutableList<Float>
    )
}
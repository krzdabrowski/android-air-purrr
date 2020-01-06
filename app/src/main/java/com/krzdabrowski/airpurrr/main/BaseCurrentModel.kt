package com.krzdabrowski.airpurrr.main

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.res.ResourcesCompat
import com.krzdabrowski.airpurrr.R

abstract class BaseCurrentModel {
    abstract fun getSource(context: Context): String

    abstract fun getDataPercentage(context: Context, type: String): String

    abstract fun getDataUgm3(context: Context, type: String): String

    abstract fun getPercentageDouble(type: String): Double

    fun getBackgroundColorDrawable(context: Context, dataPercentage: Double): Drawable? {
        return when (dataPercentage) {
            0.0 -> ResourcesCompat.getDrawable(context.resources, R.drawable.data_unavailable, null)
            in 0.1..50.0 -> ResourcesCompat.getDrawable(context.resources, R.drawable.data_green, null)
            in 50.1..100.0 -> ResourcesCompat.getDrawable(context.resources, R.drawable.data_lime, null)
            in 100.1..200.0 -> ResourcesCompat.getDrawable(context.resources, R.drawable.data_yellow, null)
            else -> ResourcesCompat.getDrawable(context.resources, R.drawable.data_red, null)
        }
    }
}
package com.krzdabrowski.airpurrr.main.detector

import android.content.Context
import com.krzdabrowski.airpurrr.R
import com.krzdabrowski.airpurrr.main.BaseForecastModel

data class DetectorForecastModel(@Transient override val result: List<Pair<String, Pair<Float, Float>>>) : BaseForecastModel() {
    data class Data(
            val date: String,
            val values: List<Pair<Double, Double>>
    )

    override fun getSource(context: Context): String {
        return context.getString(R.string.main_data_info_indoors)
    }
}
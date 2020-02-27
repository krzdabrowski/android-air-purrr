package com.krzdabrowski.airpurrr.main.api

import android.content.Context
import com.krzdabrowski.airpurrr.R
import com.krzdabrowski.airpurrr.main.BaseForecastModel
import com.squareup.moshi.Json

data class ApiForecastModel(@Transient override val result: Result) : BaseForecastModel() {
    data class Data(
            @field:Json(name = "tillDateTime") val date: String?,
            val values: List<Map<String?, Any?>?>?
    )

    override fun getSource(context: Context): String {
        return context.getString(R.string.main_data_info_api) ?: context.getString(R.string.main_data_info_api_empty)
    }
}
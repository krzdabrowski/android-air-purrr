package com.krzdabrowski.airpurrr.main.api

import com.krzdabrowski.airpurrr.main.helper.ConversionHelper.formatDateToLocalTimezone
import com.krzdabrowski.airpurrr.main.helper.ConversionHelper.pm10ToPercent
import com.krzdabrowski.airpurrr.main.helper.ConversionHelper.pm25ToPercent
import retrofit2.Response

object ApiAirlyConverter {
    fun getData(response: Response<ApiModel>): Pair<ApiCurrentModel, ApiForecastModel> {
        val apiCurrentModel = getCurrentData(response)
        val apiForecastModel = getForecastData(response)
        return Pair(apiCurrentModel, apiForecastModel)
    }

    private fun getCurrentData(response: Response<ApiModel>): ApiCurrentModel {
        val currentValues = response.body()?.current?.values
        if (currentValues?.get(1)?.get("name") == "PM25" && currentValues[2]?.get("name") == "PM10") {
            val pm25 = currentValues[1]?.get("value") as Double
            val pm10 = currentValues[2]?.get("value") as Double
            return ApiCurrentModel(Pair(pm25, pm10))
        }
        return ApiCurrentModel(Pair(0.0, 0.0))
    }

    private fun getForecastData(response: Response<ApiModel>): ApiForecastModel {
        val forecasts = response.body()?.forecast ?: return ApiForecastModel(listOf())

        val listOfForecastData = mutableListOf<Pair<String, Pair<Float, Float>>>()
        for (forecast in forecasts) {
            val date = forecast?.date ?: ""
            val values = forecast?.values
            var valuesPair = Pair(0f, 0f)

            if (values?.get(0)?.get("name") == "PM25" && values?.get(1)?.get("name") == "PM10") {
                val pm25 = values[0]?.get("value") as Double
                val pm10 = values[1]?.get("value") as Double
                valuesPair = Pair(
                        pm25ToPercent(pm25).toFloat(),
                        pm10ToPercent(pm10).toFloat()
                )
            }

            val formattedDate = formatDateToLocalTimezone(date)
            listOfForecastData.add(Pair(formattedDate, valuesPair))
        }

        return ApiForecastModel(listOfForecastData)
    }
}
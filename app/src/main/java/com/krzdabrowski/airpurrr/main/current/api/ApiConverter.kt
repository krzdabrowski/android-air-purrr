package com.krzdabrowski.airpurrr.main.current.api

import retrofit2.Response

object ApiConverter {
    fun getData(response: Response<ApiModel>): ApiModel {
        val currentValues = response.body()?.current?.values
        if (currentValues?.get(1)?.get("name") == "PM25" && currentValues[2]?.get("name") == "PM10") {
            val pm25 = currentValues[1]?.get("value") as Double
            val pm10 = currentValues[2]?.get("value") as Double
            return ApiModel(null, Pair(pm25, pm10))
        }
        return ApiModel(null, Pair(0.0, 0.0))
    }
}
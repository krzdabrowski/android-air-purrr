package com.krzdabrowski.airpurrr.main.api

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import retrofit2.Response

// for verification if API hasn't changed
class ApiAirlyConverterTest {

    @Test
    fun `given currently correct API input as model, then return converted model with data`() {
        val model = ApiModel(
                ApiCurrentModel.Data(
                        mutableListOf(
                                mapOf(Pair("name", "PM1"), Pair("value", 2.5), Pair(null, "")),
                                mapOf(Pair("name", "PM25"), Pair("value", 5.0), Pair(null, "")),
                                mapOf(Pair("name", "PM10"), Pair("value", 7.5), Pair(null, ""))
                        )
                ),
                listOf(
                        ApiForecastModel.Data(
                                "2020-01-08T19:00:00.000Z",
                                mutableListOf(
                                        mapOf(Pair("name", "PM25"), Pair("value", 5.0), Pair(null, "")),
                                        mapOf(Pair("name", "PM10"), Pair("value", 7.5), Pair(null, ""))
                                )
                        ),
                        ApiForecastModel.Data(
                                "2020-01-08T20:00:00.000Z",
                                mutableListOf(
                                        mapOf(Pair("name", "PM25"), Pair("value", 10.0), Pair(null, "")),
                                        mapOf(Pair("name", "PM10"), Pair("value", 12.5), Pair(null, ""))
                                )
                        )
                )
        )
        val response = Response.success(model)

        val convertedModel = ApiAirlyConverter.getData(response)

        assertThat(convertedModel.first.result).isEqualTo(Pair(5.0, 7.5))  // PM2.5 and PM10 in ugm3
        assertThat(convertedModel.second.result).isEqualTo(listOf(
                Pair("20:00", Pair(20f, 15f)),  // PM2.5 in percentage (4x)
                Pair("21:00", Pair(40f, 25f))   // PM10 in percentage (2x)
        ))
    }

    @Test
    fun `given currently incorrect API input as model, then return converted model with zeroes`() {
        val model = ApiModel(
                ApiCurrentModel.Data(
                        mutableListOf(
                                mapOf(Pair("name", "PM1"), Pair("value", 2.5), Pair(null, "")),
                                mapOf(Pair("name", "something else"), Pair("value", 5.0), Pair(null, "")),
                                mapOf(Pair("name", "PM10"), Pair("value", 7.5), Pair(null, ""))
                        )
                ),
                listOf(
                        ApiForecastModel.Data(
                                "2020-01-08T19:00:00.000Z",
                                mutableListOf(
                                        mapOf(Pair("name", "PM25"), Pair("value", 5.0), Pair(null, "")),
                                        mapOf(Pair("name", "something else"), Pair("value", 7.5), Pair(null, ""))
                                )
                        ),
                        ApiForecastModel.Data(
                                "2020-01-08T20:00:00.000Z",
                                mutableListOf(
                                        mapOf(Pair("name", "something else"), Pair("value", 10.0), Pair(null, "")),
                                        mapOf(Pair("name", "PM10"), Pair("value", 12.5), Pair(null, ""))
                                )
                        )
                )
        )
        val response = Response.success(model)

        val convertedModel = ApiAirlyConverter.getData(response)

        assertThat(convertedModel.first.result).isEqualTo(Pair(0.0, 0.0))
        assertThat(convertedModel.second.result).isEqualTo(listOf(Pair("20:00", Pair(0f, 0f)), Pair("21:00", Pair(0f, 0f))))
    }
}
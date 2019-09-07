package com.krzdabrowski.airpurrr.main.current.api

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import retrofit2.Response

// for verification if API hasn't changed
class ApiAirlyConverterTest {

    @Test
    fun `given currently correct API input as model, then return converted model with data`() {
        val model = ApiModel(
                ApiModel.Values(
                        mutableListOf(
                                mapOf(Pair("name", "PM1"), Pair("value", 2.5), Pair(null, "")),
                                mapOf(Pair("name", "PM25"), Pair("value", 5.0), Pair(null, "")),
                                mapOf(Pair("name", "PM10"), Pair("value", 7.5), Pair(null, ""))
                        )
                ),
                Pair(-1.0, -1.0))
        val response = Response.success(model)

        val convertedModel = ApiAirlyConverter.getData(response)

        assertThat(convertedModel.data).isEqualTo(Pair(5.0, 7.5))
    }

    @Test
    fun `given currently incorrect API input as model, then return converted model with zeroes`() {
        val model = ApiModel(
                ApiModel.Values(
                        mutableListOf(
                                mapOf(Pair("name", "PM1"), Pair("value", 2.5), Pair(null, "")),
                                mapOf(Pair("name", "something else"), Pair("value", 5.0), Pair(null, "")),
                                mapOf(Pair("name", "PM10"), Pair("value", 7.5), Pair(null, ""))
                        )
                ),
                Pair(-1.0, -1.0))
        val response = Response.success(model)

        val convertedModel = ApiAirlyConverter.getData(response)

        assertThat(convertedModel.data).isEqualTo(Pair(0.0, 0.0))
    }
}
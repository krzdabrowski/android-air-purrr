package com.krzdabrowski.airpurrr.main.helper

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ConversionHelperTest {

    @Test
    fun `given non-null PM25 in ugm3, then PM25 in percentage is 4x bigger`() {
        val pm25InUgm3 = 2.5

        val pm25InPercentage = ConversionHelper.pm25ToPercent(pm25InUgm3)

        assertThat(pm25InPercentage).isEqualTo(10.0)
    }

    @Test
    fun `given null PM25 in ugm3, then PM25 in percentage is 0`() {
        val pm25InUgm3 = null

        val pm25InPercentage = ConversionHelper.pm25ToPercent(pm25InUgm3)

        assertThat(pm25InPercentage).isEqualTo(0.0)
    }

    @Test
    fun `given non-null PM10 in ugm3, then PM10 in percentage is 2x bigger`() {
        val pm10InUgm3 = 2.5

        val pm10InPercentage = ConversionHelper.pm10ToPercent(pm10InUgm3)

        assertThat(pm10InPercentage).isEqualTo(5.0)
    }

    @Test
    fun `given null PM10 in ugm3, then PM25 in percentage is 0`() {
        val pm10InUgm3 = null

        val pm10InPercentage = ConversionHelper.pm10ToPercent(pm10InUgm3)

        assertThat(pm10InPercentage).isEqualTo(0.0)
    }
}
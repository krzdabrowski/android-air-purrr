package com.krzdabrowski.airpurrr.main

import android.location.Location
import com.google.common.truth.Truth.assertThat
import com.krzdabrowski.airpurrr.main.current.api.ApiAirlyConverter
import com.krzdabrowski.airpurrr.main.current.api.ApiModel
import com.krzdabrowski.airpurrr.main.current.api.ApiRepository
import com.krzdabrowski.airpurrr.main.current.api.ApiService
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Response

@ExperimentalCoroutinesApi
class CurrentApiRepositoryTest {
    private lateinit var apiRepository: ApiRepository

    @MockK
    private lateinit var apiService: ApiService

    @MockK
    private lateinit var location: Location

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        mockkObject(ApiAirlyConverter)
        apiRepository = ApiRepository(apiService)
    }

    @Test
    fun `given response body is not null, when fetching data, then model is not null`() = runBlockingTest {
        val body = ApiModel(ApiModel.Values(mutableListOf()), Pair(5.0, 7.5))

        every { ApiAirlyConverter.getData(any()) } returns body
        every { location.latitude } returns 50.0
        every { location.longitude } returns 20.0
        coEvery { apiService.getApiDataAsync(any(), any(), any()) } returns Response.success(body)

        assertThat(apiRepository.fetchData(location)).isNotNull()
        assertThat(apiRepository.fetchData(location)).isEqualTo(body)

        coVerify { apiService.getApiDataAsync(any(), any(), any()) }
    }

    @Test
    fun `given response body is null, when fetching data, then model is null`() = runBlockingTest {
        val body = null

        every { location.latitude } returns 50.0
        every { location.longitude } returns 20.0
        coEvery { apiService.getApiDataAsync(any(), any(), any()) } returns Response.success(body)

        assertThat(apiRepository.fetchData(location)).isNull()

        coVerify { apiService.getApiDataAsync(any(), any(), any()) }
    }

    @Test
    fun `given exception, when fetching data, then model is null`() = runBlockingTest {
        every { location.latitude } returns 50.0
        every { location.longitude } returns 20.0
        coEvery { apiService.getApiDataAsync(any(), any(), any()) } throws Throwable()

        assertThat(apiRepository.fetchData(location)).isNull()

        coVerify { apiService.getApiDataAsync(any(), any(), any()) }
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }
}
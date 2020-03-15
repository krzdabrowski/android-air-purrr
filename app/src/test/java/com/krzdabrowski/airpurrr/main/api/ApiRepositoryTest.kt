package com.krzdabrowski.airpurrr.main.api

import android.location.Location
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Response

@ExperimentalCoroutinesApi
class ApiRepositoryTest {
    private lateinit var apiRepository: ApiRepository

    @MockK
    private lateinit var apiService: ApiService

    @MockK
    private lateinit var location: Location

    @MockK
    private lateinit var apiModel: ApiModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        every { location.latitude } returns 50.0
        every { location.longitude } returns 20.0

        apiRepository = ApiRepository(apiService)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `given response body is not null, when fetching data, then model is not null`() = runBlockingTest {
        coEvery { apiService.getApiDataAsync(any(), any(), any()) } returns Response.success(apiModel)

        assertThat(apiService.getApiDataAsync("", location.latitude, location.longitude).isSuccessful).isTrue()
        assertThat(apiService.getApiDataAsync("", location.latitude, location.longitude)).isNotNull()

        coVerify { apiService.getApiDataAsync(any(), any(), any()) }
    }

    @Test
    fun `given response body is not null, when fetching data, then model is body`() = runBlockingTest {
        coEvery { apiService.getApiDataAsync(any(), any(), any()) } returns Response.success(apiModel)

        assertThat(apiService.getApiDataAsync("", location.latitude, location.longitude).isSuccessful).isTrue()
        assertThat(apiService.getApiDataAsync("", location.latitude, location.longitude).body()).isEqualTo(apiModel)

        coVerify { apiService.getApiDataAsync(any(), any(), any()) }
    }
}
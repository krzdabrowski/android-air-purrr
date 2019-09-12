package com.krzdabrowski.airpurrr.main.current.api

import android.location.Location
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
        mockkObject(ApiAirlyConverter)

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
        every { ApiAirlyConverter.getData(any()) } returns apiModel
        coEvery { apiService.getApiDataAsync(any(), any(), any()) } returns Response.success(apiModel)

        assertThat(apiService.getApiDataAsync("", location.latitude, location.longitude).isSuccessful).isTrue()
        assertThat(apiRepository.fetchData(location)).isNotNull()

        coVerify { apiService.getApiDataAsync(any(), any(), any()) }
    }

    @Test
    fun `given response body is not null, when fetching data, then model is body`() = runBlockingTest {
        every { ApiAirlyConverter.getData(any()) } returns apiModel
        coEvery { apiService.getApiDataAsync(any(), any(), any()) } returns Response.success(apiModel)

        assertThat(apiService.getApiDataAsync("", location.latitude, location.longitude).isSuccessful).isTrue()
        assertThat(apiRepository.fetchData(location)).isEqualTo(apiModel)

        coVerify { apiService.getApiDataAsync(any(), any(), any()) }
    }

    @Test
    fun `given response body is null, when fetching data, then model is null`() = runBlockingTest {
        coEvery { apiService.getApiDataAsync(any(), any(), any()) } returns Response.success(null)

        assertThat(apiService.getApiDataAsync("", location.latitude, location.longitude).isSuccessful).isTrue()
        assertThat(apiRepository.fetchData(location)).isNull()

        coVerify { apiService.getApiDataAsync(any(), any(), any()) }
    }

    @Test
    fun `given exception, when fetching data, then model is null`() = runBlockingTest {
        coEvery { apiService.getApiDataAsync(any(), any(), any()) } throws Throwable()

        assertThat(apiRepository.fetchData(location)).isNull()

        coVerify { apiService.getApiDataAsync(any(), any(), any()) }
    }
}
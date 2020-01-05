package com.krzdabrowski.airpurrr.main.current.api

import android.location.Location
import com.google.common.truth.Truth.assertThat
import com.krzdabrowski.airpurrr.main.api.ApiAirlyConverter
import com.krzdabrowski.airpurrr.main.api.ApiCurrentModel
import com.krzdabrowski.airpurrr.main.api.ApiRepository
import com.krzdabrowski.airpurrr.main.api.ApiService
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
    private lateinit var apiCurrentModel: ApiCurrentModel

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
        lateinit var response: Response<ApiCurrentModel>
        every { ApiAirlyConverter.getData(any()) } returns apiCurrentModel
        coEvery { apiService.getApiDataAsync(any(), any(), any()) } returns flowOf(Response.success(apiCurrentModel))

        apiService.getApiDataAsync("", location.latitude, location.longitude).collect { res -> response = res }

        assertThat(response.isSuccessful).isTrue()
        assertThat(response.body()).isNotNull()

        coVerify { apiService.getApiDataAsync(any(), any(), any()) }
    }

    @Test
    fun `given response body is not null, when fetching data, then model is body`() = runBlockingTest {
        lateinit var response: Response<ApiCurrentModel>
        every { ApiAirlyConverter.getData(any()) } returns apiCurrentModel
        coEvery { apiService.getApiDataAsync(any(), any(), any()) } returns flowOf(Response.success(apiCurrentModel))

        apiService.getApiDataAsync("", location.latitude, location.longitude).collect { res -> response = res }

        assertThat(response.isSuccessful).isTrue()
        assertThat(response.body()).isEqualTo(apiCurrentModel)

        coVerify { apiService.getApiDataAsync(any(), any(), any()) }
    }
}
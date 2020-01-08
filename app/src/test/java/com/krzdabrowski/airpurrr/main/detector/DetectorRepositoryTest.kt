package com.krzdabrowski.airpurrr.main.detector

import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
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
class DetectorRepositoryTest {
    private lateinit var detectorRepository: DetectorRepository

    @MockK
    private lateinit var httpService: DetectorDataService

    @MockK
    private lateinit var httpsService: DetectorControlService

    @MockK
    private lateinit var detectorCurrentModel: DetectorCurrentModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        detectorRepository = DetectorRepository(httpService, httpsService)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `given response body is not null, when fetching data, then model is not null`() = runBlockingTest {
        lateinit var response: Response<DetectorCurrentModel>
        coEvery { httpService.getDetectorDataAsync() } returns flowOf(Response.success(detectorCurrentModel))

        httpService.getDetectorDataAsync().collect { res -> response = res }

        assertThat(response.isSuccessful).isTrue()
        assertThat(response.body()).isNotNull()

        coVerify { httpService.getDetectorDataAsync() }
    }

    @Test
    fun `given response body is not null, when fetching data, then model is body`() = runBlockingTest {
        lateinit var response: Response<DetectorCurrentModel>
        coEvery { httpService.getDetectorDataAsync() } returns flowOf(Response.success(detectorCurrentModel))

        httpService.getDetectorDataAsync().collect { res -> response = res }

        assertThat(response.isSuccessful).isTrue()
        assertThat(response.body()).isEqualTo(detectorCurrentModel)

        coVerify { httpService.getDetectorDataAsync() }
    }
}
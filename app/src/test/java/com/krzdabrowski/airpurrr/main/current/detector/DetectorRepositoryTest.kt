package com.krzdabrowski.airpurrr.main.current.detector

import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
    private lateinit var detectorModel: DetectorModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        detectorRepository = DetectorRepository(httpService, httpsService)
    }

    @Test
    fun `given response body is not null, when fetching data, then model is not null`() = runBlockingTest {
        coEvery { httpService.getDetectorDataAsync() } returns Response.success(detectorModel)

        assertThat(detectorRepository.fetchData()).isNotNull()
        assertThat(detectorRepository.fetchData()).isEqualTo(detectorModel)

        coVerify { httpService.getDetectorDataAsync() }
    }

    @Test
    fun `given response body is null, when fetching data, then model is null`() = runBlockingTest {
        coEvery { httpService.getDetectorDataAsync() } returns Response.success(null)

        assertThat(detectorRepository.fetchData()).isNull()

        coVerify { httpService.getDetectorDataAsync() }
    }

    @Test
    fun `given exception, when fetching data, then model is null`() = runBlockingTest {
        coEvery { httpService.getDetectorDataAsync() } throws Throwable()

        assertThat(detectorRepository.fetchData()).isNull()

        coVerify { httpService.getDetectorDataAsync() }
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }
}
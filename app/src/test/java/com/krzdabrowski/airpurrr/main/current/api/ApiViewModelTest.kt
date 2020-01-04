package com.krzdabrowski.airpurrr.main.current.api

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.asLiveData
import com.google.common.truth.Truth.assertThat
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class ApiViewModelTest {
    private lateinit var apiViewModel: ApiViewModel

    @MockK
    private lateinit var apiRepository: ApiRepository

    @MockK
    private lateinit var apiModel: ApiModel

    @get:Rule
    val aacSyncRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(TestCoroutineDispatcher())

        coEvery { apiRepository.fetchDataFlow(any()) } returns flowOf(apiModel)

        apiViewModel = ApiViewModel(apiRepository)
        apiViewModel.liveData.observeForever {}
    }

    @After
    fun tearDown() {
        clearAllMocks()
        Dispatchers.resetMain()
    }

    @Test
    fun `when fetching data successfully, then proper data is saved`() = runBlockingTest {
        coEvery { apiRepository.fetchDataFlow(any()).asLiveData().value } returns apiModel

        coVerify { apiRepository.fetchDataFlow(any()).asLiveData() }

        assertThat(apiViewModel.liveData.value).isEqualTo(apiModel)
    }
}
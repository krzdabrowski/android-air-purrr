package com.krzdabrowski.airpurrr.main.current.api

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
        apiViewModel = ApiViewModel(apiRepository)
    }

    @After
    fun tearDown() {
        clearAllMocks()
        Dispatchers.resetMain()
    }

    @Test
    fun `when fetching data successfully, then check if repository was called`() = runBlockingTest {
        coEvery { apiRepository.fetchData(any()) } returns apiModel

        apiViewModel.getLiveData().observeForever {}
        apiViewModel.getLiveData()

        coVerify { apiRepository.fetchData(any()) }
    }
}
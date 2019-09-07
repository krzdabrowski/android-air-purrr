package com.krzdabrowski.airpurrr.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.krzdabrowski.airpurrr.main.current.api.ApiModel
import com.krzdabrowski.airpurrr.main.current.api.ApiRepository
import com.krzdabrowski.airpurrr.main.current.api.ApiViewModel
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class CurrentApiViewModelTest {
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
        // Dispatchers.setMain(Dispatchers.Unconfined)
        apiViewModel = ApiViewModel(apiRepository)
    }

    // flaky test due to LiveData 2.2.0 alpha version:
    // * reverting to state before update makes test no more flaky
    // * but vastly increases boilerplate in many classes
    // * using runBlockingTest without Unconfined passes the test with Dispatchers.setMain exception (so also coroutines bug?)
    @Test
    fun `when fetching data successfully, then check if repository was called`() = runBlockingTest {
        coEvery { apiRepository.fetchData(any()) } returns apiModel

        apiViewModel.getLiveData().observeForever {}
        apiViewModel.getLiveData()

        coVerify { apiRepository.fetchData(any()) }
    }

    @After
    fun tearDown() {
        clearAllMocks()
        // Dispatchers.resetMain()
    }
}
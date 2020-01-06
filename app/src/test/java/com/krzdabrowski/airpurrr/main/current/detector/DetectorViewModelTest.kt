package com.krzdabrowski.airpurrr.main.current.detector

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.databinding.Observable
import androidx.lifecycle.asLiveData
import com.google.common.truth.Truth.assertThat
import com.krzdabrowski.airpurrr.main.detector.DetectorCurrentModel
import com.krzdabrowski.airpurrr.main.detector.DetectorRepository
import com.krzdabrowski.airpurrr.main.detector.DetectorViewModel
import io.mockk.*
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
class DetectorViewModelTest {
    private lateinit var detectorViewModel: DetectorViewModel

    @MockK
    private lateinit var detectorRepository: DetectorRepository

    @get:Rule
    val aacSyncRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(TestCoroutineDispatcher())

        val model = DetectorCurrentModel("WorkStates.Sleeping", DetectorCurrentModel.Data(5.0, 7.5))
        coEvery { detectorRepository.fetchDataFlow() } returns flowOf(model)

        detectorViewModel = DetectorViewModel(detectorRepository)
        detectorViewModel.liveData.observeForever {}
    }

    @After
    fun tearDown() {
        clearAllMocks()
        Dispatchers.resetMain()
    }

    @Test
    fun `when fetching data successfully, then proper data is saved`() = runBlockingTest {
        val model = DetectorCurrentModel("WorkStates.Sleeping", DetectorCurrentModel.Data(5.0, 7.5))
        coEvery { detectorRepository.fetchDataFlow().asLiveData().value } returns model

        coVerify { detectorRepository.fetchDataFlow().asLiveData() }

        assertThat(detectorViewModel.liveData.value?.workstate).isEqualTo(model.workstate)
        assertThat(detectorViewModel.liveData.value?.values).isEqualTo(model.values)
    }

    @Test
    fun `given purifier is OFF & auto is ON & threshold is LOW, then purifier is turning on`() {
        setFields(state = false, autoMode = true, autoThreshold = 0)

        detectorViewModel.checkAutoMode()

        // state changes
        assertThat(detectorViewModel.purifierOnOffObservableState.get()).isTrue()
    }

    @Test
    fun `given purifier is ON & auto is ON & threshold is LOW, then purifier keeps working on`() {
        setFields(state = true, autoMode = true, autoThreshold = 0)

        detectorViewModel.checkAutoMode()

        // state doesn't change
        assertThat(detectorViewModel.purifierOnOffObservableState.get()).isTrue()
    }

    @Test
    fun `given purifier is OFF & auto is OFF & threshold is LOW, then purifier doesn't turn on`() {
        setFields(state = false, autoMode = false, autoThreshold = 0)

        detectorViewModel.checkAutoMode()

        // state doesn't change
        assertThat(detectorViewModel.purifierOnOffObservableState.get()).isFalse()
    }

    @Test
    fun `given purifier is ON & auto is OFF & threshold is LOW, then purifier is turning off`() {
        setFields(state = true, autoMode = false, autoThreshold = 0)

        detectorViewModel.checkAutoMode()

        // state changes
        assertThat(detectorViewModel.purifierOnOffObservableState.get()).isFalse()
    }

    @Test
    fun `given purifier is OFF & auto is ON & threshold is HIGH, then purifier doesn't turn on`() {
        setFields(state = false, autoMode = true, autoThreshold = 100)

        detectorViewModel.checkAutoMode()

        // state doesn't change
        assertThat(detectorViewModel.purifierOnOffObservableState.get()).isFalse()
    }

    @Test
    fun `given purifier is ON & auto is ON & threshold is HIGH, then purifier keeps working on`() {
        setFields(state = true, autoMode = true, autoThreshold = 100)

        detectorViewModel.checkAutoMode()

        // state doesn't change
        assertThat(detectorViewModel.purifierOnOffObservableState.get()).isTrue()
    }

    @Test
    fun `given purifier is OFF & auto is OFF & threshold is HIGH, then purifier doesn't turn on`() {
        setFields(state = false, autoMode = false, autoThreshold = 100)

        detectorViewModel.checkAutoMode()

        // state doesn't change
        assertThat(detectorViewModel.purifierOnOffObservableState.get()).isFalse()
    }

    @Test
    fun `given purifier is ON & auto is OFF & threshold is HIGH, then purifier is turning off`() {
        setFields(state = true, autoMode = false, autoThreshold = 100)

        detectorViewModel.checkAutoMode()

        // state changes
        assertThat(detectorViewModel.purifierOnOffObservableState.get()).isFalse()
    }

    @Test
    fun `given purifier is ON & auto is OFF & threshold is HIGH, when purifier is going to be turned on, then observable notifies change`() {
        // Arrange
        setFields(state = false, autoMode = true, autoThreshold = 0)

        val listener = mockkClass(Observable.OnPropertyChangedCallback::class)
        detectorViewModel.purifierOnOffObservableState.addOnPropertyChangedCallback(listener)

        every { listener.onPropertyChanged(detectorViewModel.purifierOnOffObservableState, any()) } just Runs

        // Act
        detectorViewModel.checkAutoMode()

        // Assert
        verify { listener.onPropertyChanged(detectorViewModel.purifierOnOffObservableState, any()) }
    }

    @Test
    fun `given purifier is ON & auto is OFF & threshold is LOW, when purifier is going to be turned off, then observable notifies change`() {
        // Arrange
        setFields(state = true, autoMode = false, autoThreshold = 0)

        val listener = mockkClass(Observable.OnPropertyChangedCallback::class)
        detectorViewModel.purifierOnOffObservableState.addOnPropertyChangedCallback(listener)

        every { listener.onPropertyChanged(detectorViewModel.purifierOnOffObservableState, any()) } just Runs

        // Act
        detectorViewModel.checkAutoMode()

        // Assert
        verify { listener.onPropertyChanged(detectorViewModel.purifierOnOffObservableState, any()) }
    }

    @Test
    fun `given purifier is ON & auto is OFF & threshold is HIGH, when purifier is going to be turned off, then observable notifies change`() {
        // Arrange
        setFields(state = true, autoMode = false, autoThreshold = 100)

        val listener = mockkClass(Observable.OnPropertyChangedCallback::class)
        detectorViewModel.purifierOnOffObservableState.addOnPropertyChangedCallback(listener)

        every { listener.onPropertyChanged(detectorViewModel.purifierOnOffObservableState, any()) } just Runs

        // Act
        detectorViewModel.checkAutoMode()

        // Assert
        verify { listener.onPropertyChanged(detectorViewModel.purifierOnOffObservableState, any()) }
    }

    @Test
    fun `given purifier is OFF, when performance mode is going to be turned on, then request is not sent`() {
        // Arrange
        setFields(state = false, autoMode = false, autoThreshold = 0)

        // Act
        detectorViewModel.checkPerformanceMode(shouldSwitchToHigh = true)

        // Assert
        verify (exactly = 0) { detectorViewModel.controlFanHighLow(true) }
    }

    @Test
    fun `given purifier is OFF, when night mode is going to be turned on, then request is not sent`() {
        // Arrange
        setFields(state = false, autoMode = false, autoThreshold = 0)

        // Act
        detectorViewModel.checkPerformanceMode(shouldSwitchToHigh = false)

        // Assert
        verify (exactly = 0) { detectorViewModel.controlFanHighLow(false) }
    }

    @Test
    fun `given purifier is ON, when performance mode is going to be turned on, then request is sent`() {
        // Arrange
        setFields(state = true, autoMode = false, autoThreshold = 0)
        every { detectorViewModel.controlFanHighLow(true) } just Runs

        // Act
        detectorViewModel.checkPerformanceMode(shouldSwitchToHigh = true)

        // Assert
        verify { detectorViewModel.controlFanHighLow(true) }
    }

    @Test
    fun `given purifier is ON, when night mode is going to be turned on, then request is sent`() {
        // Arrange
        setFields(state = true, autoMode = false, autoThreshold = 0)
        every { detectorViewModel.controlFanHighLow(false) } just Runs

        // Act
        detectorViewModel.checkPerformanceMode(shouldSwitchToHigh = false)

        // Assert
        verify { detectorViewModel.controlFanHighLow(false) }
    }

    private fun setFields(state: Boolean, autoMode: Boolean, autoThreshold: Int) {
        with (detectorViewModel) {
            purifierOnOffObservableState.set(state)
            purifierOnOffState = state
            autoModeSwitch.set(autoMode)
            autoModeThreshold.set(autoThreshold)
        }
    }
}
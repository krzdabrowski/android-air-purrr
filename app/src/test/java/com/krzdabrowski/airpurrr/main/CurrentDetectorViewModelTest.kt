package com.krzdabrowski.airpurrr.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.databinding.Observable
import com.google.common.truth.Truth.assertThat
import com.krzdabrowski.airpurrr.main.current.detector.DetectorModel
import com.krzdabrowski.airpurrr.main.current.detector.DetectorRepository
import com.krzdabrowski.airpurrr.main.current.detector.DetectorViewModel
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class CurrentDetectorViewModelTest {
    private lateinit var detectorViewModel: DetectorViewModel

    @MockK
    private lateinit var detectorRepository: DetectorRepository

    @get:Rule
    val aacSyncRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(Dispatchers.Unconfined)
        detectorViewModel = DetectorViewModel(detectorRepository)
    }

    @Test  // FLAKY!!!
    fun `when fetching data successfully, then proper data is saved`() {
        val model = DetectorModel("WorkStates.Sleeping", DetectorModel.Values(1.0, 3.5))
        coEvery { detectorRepository.fetchData() } returns model

        detectorViewModel.getLiveData().observeForever {}

        coVerify { detectorRepository.fetchData() }

        assertThat(detectorViewModel.data?.workstate).isEqualTo(model.workstate)
        assertThat(detectorViewModel.data?.values).isEqualTo(model.values)
    }


    @Test
    fun `given purifier is OFF & auto is ON & threshold is LOW, then purifier is turning on`() {
        setFields(purifierState = false, autoModeSwitch = true, autoModeThreshold = 0)

        detectorViewModel.checkAutoMode()

        // state changes
        assertThat(detectorViewModel.purifierObservableState.get()).isTrue()
    }

    @Test
    fun `given purifier is ON & auto is ON & threshold is LOW, then purifier keeps working on`() {
        setFields(purifierState = true, autoModeSwitch = true, autoModeThreshold = 0)

        detectorViewModel.checkAutoMode()

        // state doesn't change
        assertThat(detectorViewModel.purifierObservableState.get()).isTrue()
    }

    @Test
    fun `given purifier is OFF & auto is OFF & threshold is LOW, then purifier doesn't turn on`() {
        setFields(purifierState = false, autoModeSwitch = false, autoModeThreshold = 0)

        detectorViewModel.checkAutoMode()

        // state doesn't change
        assertThat(detectorViewModel.purifierObservableState.get()).isFalse()
    }

    @Test
    fun `given purifier is ON & auto is OFF & threshold is LOW, then purifier is turning off`() {
        setFields(purifierState = true, autoModeSwitch = false, autoModeThreshold = 0)

        detectorViewModel.checkAutoMode()

        // state changes
        assertThat(detectorViewModel.purifierObservableState.get()).isFalse()
    }

    @Test
    fun `given purifier is OFF & auto is ON & threshold is HIGH, then purifier doesn't turn on`() {
        setFields(purifierState = false, autoModeSwitch = true, autoModeThreshold = 100)

        detectorViewModel.checkAutoMode()

        // state doesn't change
        assertThat(detectorViewModel.purifierObservableState.get()).isFalse()
    }

    @Test
    fun `given purifier is ON & auto is ON & threshold is HIGH, then purifier keeps working on`() {
        setFields(purifierState = true, autoModeSwitch = true, autoModeThreshold = 100)

        detectorViewModel.checkAutoMode()

        // state doesn't change
        assertThat(detectorViewModel.purifierObservableState.get()).isTrue()
    }

    @Test
    fun `given purifier is OFF & auto is OFF & threshold is HIGH, then purifier doesn't turn on`() {
        setFields(purifierState = false, autoModeSwitch = false, autoModeThreshold = 100)

        detectorViewModel.checkAutoMode()

        // state doesn't change
        assertThat(detectorViewModel.purifierObservableState.get()).isFalse()
    }

    @Test
    fun `given purifier is ON & auto is OFF & threshold is HIGH, then purifier is turning off`() {
        setFields(purifierState = true, autoModeSwitch = false, autoModeThreshold = 100)

        detectorViewModel.checkAutoMode()

        // state changes
        assertThat(detectorViewModel.purifierObservableState.get()).isFalse()
    }

    @Test
    fun `given purifier is ON & auto is OFF & threshold is HIGH, when purifier is going to be turned on, then observable notifies change`() {
        // Arrange
        setFields(purifierState = false, autoModeSwitch = true, autoModeThreshold = 0)

        val listener = mockkClass(Observable.OnPropertyChangedCallback::class)
        detectorViewModel.purifierObservableState.addOnPropertyChangedCallback(listener)

        every { listener.onPropertyChanged(detectorViewModel.purifierObservableState, any()) } just Runs

        // Act
        detectorViewModel.checkAutoMode()

        // Assert
        verify { listener.onPropertyChanged(detectorViewModel.purifierObservableState, any()) }
    }

    @Test
    fun `given purifier is ON & auto is OFF & threshold is LOW, when purifier is going to be turned off, then observable notifies change`() {
        // Arrange
        setFields(purifierState = true, autoModeSwitch = false, autoModeThreshold = 0)

        val listener = mockkClass(Observable.OnPropertyChangedCallback::class)
        detectorViewModel.purifierObservableState.addOnPropertyChangedCallback(listener)

        every { listener.onPropertyChanged(detectorViewModel.purifierObservableState, any()) } just Runs

        // Act
        detectorViewModel.checkAutoMode()

        // Assert
        verify { listener.onPropertyChanged(detectorViewModel.purifierObservableState, any()) }
    }

    @Test
    fun `given purifier is ON & auto is OFF & threshold is HIGH, when purifier is going to be turned off, then observable notifies change`() {
        // Arrange
        setFields(purifierState = true, autoModeSwitch = false, autoModeThreshold = 100)

        val listener = mockkClass(Observable.OnPropertyChangedCallback::class)
        detectorViewModel.purifierObservableState.addOnPropertyChangedCallback(listener)

        every { listener.onPropertyChanged(detectorViewModel.purifierObservableState, any()) } just Runs

        // Act
        detectorViewModel.checkAutoMode()

        // Assert
        verify { listener.onPropertyChanged(detectorViewModel.purifierObservableState, any()) }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun setFields(purifierState: Boolean, autoModeSwitch: Boolean, autoModeThreshold: Int) {
        detectorViewModel.data = DetectorModel("WorkStates.Sleeping", DetectorModel.Values(5.0, 7.5))

        detectorViewModel.purifierObservableState.set(purifierState)
        detectorViewModel.purifierState = purifierState
        detectorViewModel.autoModeSwitch.set(autoModeSwitch)
        detectorViewModel.autoModeThreshold.set(autoModeThreshold)
    }
}
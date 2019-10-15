package com.krzdabrowski.airpurrr.main

import com.krzdabrowski.airpurrr.main.current.detector.DetectorModel
import com.krzdabrowski.airpurrr.main.current.detector.DetectorViewModel
import io.mockk.*
import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Test

class PurifierHelperTest {
    private lateinit var purifierHelper: PurifierHelper
    private val currentState = false

    @MockK
    private lateinit var detectorViewModel: DetectorViewModel

    @MockK
    private lateinit var detectorModel: DetectorModel

    @MockK
    private lateinit var listener: PurifierHelper.SnackbarListener

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        every { detectorViewModel.controlFanOnOff(any(), any(), any()) } just Runs

        purifierHelper = PurifierHelper(detectorViewModel)
        purifierHelper.listener = listener
        every { listener.showSnackbar(any(), any()) } just Runs
    }

    @Test
    fun `when purifier state is in sleeping mode, then purifier can be controlled with new state`(){
        every { detectorModel.workstate } returns "WorkStates.Sleeping"

        purifierHelper.getPurifierOnOffState(detectorModel, "some@email.com", "password", currentState)

        verify { detectorViewModel.controlFanOnOff(!currentState, any(), any()) }
    }

    @Test
    fun `when purifier state is in measuring mode, then purifier can't be controlled`(){
        every { detectorModel.workstate } returns "WorkStates.Measuring"

        purifierHelper.getPurifierOnOffState(detectorModel, "some@email.com", "password", currentState)

        verify (exactly = 0) { detectorViewModel.controlFanOnOff(!currentState, any(), any()) }
    }

    @Test
    fun `when purifier state is in undefined mode, then purifier can't be controlled`(){
        every { detectorModel.workstate } returns ""

        purifierHelper.getPurifierOnOffState(detectorModel, "some@email.com", "password", currentState)

        verify (exactly = 0) { detectorViewModel.controlFanOnOff(!currentState, any(), any()) }
    }
}
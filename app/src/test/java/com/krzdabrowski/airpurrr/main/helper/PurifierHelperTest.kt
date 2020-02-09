package com.krzdabrowski.airpurrr.main.helper

import com.krzdabrowski.airpurrr.main.detector.DetectorViewModel
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
    private lateinit var listener: PurifierHelper.SnackbarListener

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        every { detectorViewModel.controlFanOnOff(any()) } just Runs

        purifierHelper = PurifierHelper(detectorViewModel)
        purifierHelper.snackbarListener = listener
        every { listener.showSnackbar(any(), any()) } just Runs
    }

    @Test
    fun `when purifier state is in sleeping mode, then purifier can be controlled with new state`(){
        val workstate = PurifierHelper.Workstates.SLEEPING.state

        purifierHelper.getPurifierOnOffState(workstate, currentState)

        verify { detectorViewModel.controlFanOnOff(!currentState) }
    }

    @Test
    fun `when purifier state is in measuring mode, then purifier can't be controlled`(){
        val workstate = PurifierHelper.Workstates.MEASURING.state

        purifierHelper.getPurifierOnOffState(workstate, currentState)

        verify (exactly = 0) { detectorViewModel.controlFanOnOff(!currentState) }
    }

    @Test
    fun `when purifier state is in undefined mode, then purifier can't be controlled`(){
        val workstate = ""

        purifierHelper.getPurifierOnOffState(workstate, currentState)

        verify (exactly = 0) { detectorViewModel.controlFanOnOff(!currentState) }
    }
}
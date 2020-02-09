package com.krzdabrowski.airpurrr.main.helper

import com.krzdabrowski.airpurrr.R
import com.krzdabrowski.airpurrr.main.detector.DetectorCurrentModel
import com.krzdabrowski.airpurrr.main.detector.DetectorViewModel
import com.google.android.material.snackbar.Snackbar

class PurifierHelper(private val detectorViewModel: DetectorViewModel) {
    internal lateinit var snackbarListener: SnackbarListener

    private enum class Workstates(val state: String) {
        SLEEPING("WorkStates.Sleeping"),
        MEASURING("WorkStates.Measuring")
    }

    fun getPurifierOnOffState(workstate: String, currentState: Boolean): Boolean {
        return when (workstate) {
            Workstates.SLEEPING.state -> {
                snackbarListener.showSnackbar(R.string.main_msg_turn_on)
                detectorViewModel.controlFanOnOff(!currentState)
                !currentState
            }
            Workstates.MEASURING.state -> {
                snackbarListener.showSnackbar(R.string.main_error_measuring)
                currentState
            }
            else -> {
                snackbarListener.showSnackbar(R.string.main_error_basic)
                currentState
            }
        }
    }

    interface SnackbarListener {
        fun showSnackbar(stringId: Int, length: Int = Snackbar.LENGTH_LONG)
    }
}
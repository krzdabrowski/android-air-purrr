package com.krzdabrowski.airpurrr.main.helper

import com.krzdabrowski.airpurrr.R
import com.krzdabrowski.airpurrr.main.detector.DetectorModel
import com.krzdabrowski.airpurrr.main.detector.DetectorViewModel
import com.google.android.material.snackbar.Snackbar

class PurifierHelper(private val detectorViewModel: DetectorViewModel) {
    internal lateinit var snackbarListener: SnackbarListener

    fun getPurifierOnOffState(value: DetectorModel?, currentState: Boolean): Boolean {
        return when (value?.workstate) {
            "WorkStates.Sleeping" -> {
                snackbarListener.showSnackbar(R.string.main_msg_turn_on)
                detectorViewModel.controlFanOnOff(!currentState)
                !currentState
            }
            "WorkStates.Measuring" -> {
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
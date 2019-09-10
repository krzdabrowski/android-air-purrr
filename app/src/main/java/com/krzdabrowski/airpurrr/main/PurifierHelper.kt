package com.krzdabrowski.airpurrr.main

import com.krzdabrowski.airpurrr.R
import com.krzdabrowski.airpurrr.main.current.detector.DetectorModel
import com.krzdabrowski.airpurrr.main.current.detector.DetectorViewModel
import com.google.android.material.snackbar.Snackbar

class PurifierHelper(private val detectorViewModel: DetectorViewModel) {
    internal lateinit var listener: SnackbarListener

    fun getPurifierState(value: DetectorModel?, login: String, password: String, currentState: Boolean): Boolean {
        return when (value?.workstate) {
            "WorkStates.Sleeping" -> {
                listener.showSnackbar(R.string.main_msg_turn_on)
                detectorViewModel.controlFan(!currentState, login, password)
                !currentState
            }
            "WorkStates.Measuring" -> {
                listener.showSnackbar(R.string.main_error_measuring)
                currentState
            }
            else -> {
                listener.showSnackbar(R.string.main_error_basic)
                currentState
            }
        }
    }

    interface SnackbarListener {
        fun showSnackbar(stringId: Int, length: Int = Snackbar.LENGTH_LONG)
    }
}
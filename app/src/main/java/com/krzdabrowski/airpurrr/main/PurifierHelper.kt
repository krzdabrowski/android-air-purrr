package com.krzdabrowski.airpurrr.main

import android.view.View
import com.krzdabrowski.airpurrr.R
import com.krzdabrowski.airpurrr.main.current.detector.DetectorModel
import com.krzdabrowski.airpurrr.main.current.detector.DetectorViewModel
import com.google.android.material.snackbar.Snackbar

class PurifierHelper(private val detectorViewModel: DetectorViewModel) {

    fun getPurifierState(value: DetectorModel?, rootView: View, login: String, password: String, currentState: Boolean): Boolean {
        return when (value?.workstate) {
            "WorkStates.Sleeping" -> {
                Snackbar.make(rootView, R.string.main_msg_turn_on, Snackbar.LENGTH_LONG).show()
                detectorViewModel.controlFan(!currentState, login, password)
                !currentState
            }
            "WorkStates.Measuring" -> {
                Snackbar.make(rootView, R.string.main_error_measuring, Snackbar.LENGTH_LONG).show()
                currentState
            }
            else -> {
                Snackbar.make(rootView, R.string.main_error_basic, Snackbar.LENGTH_LONG).show()
                currentState
            }
        }
    }
}
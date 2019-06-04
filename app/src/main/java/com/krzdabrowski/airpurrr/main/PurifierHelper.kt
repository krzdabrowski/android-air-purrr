package com.krzdabrowski.airpurrr.main

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.krzdabrowski.airpurrr.R
import com.krzdabrowski.airpurrr.main.current.detector.DetectorModel
import com.krzdabrowski.airpurrr.main.current.detector.DetectorViewModel
import com.google.android.material.snackbar.Snackbar

class PurifierHelper(private val detectorViewModel: DetectorViewModel) {

    fun getPurifierState(value: DetectorModel, login: String, password: String, previousState: Boolean, rootView: SwipeRefreshLayout): Boolean {
        return when (value.workstate) {
            "WorkStates.Sleeping" -> {
                Snackbar.make(rootView, R.string.main_msg_turn_on, Snackbar.LENGTH_LONG).show()
                detectorViewModel.controlFan(!previousState, login, password)
                !previousState
            }
            "WorkStates.Measuring" -> {
                Snackbar.make(rootView, R.string.main_error_measuring, Snackbar.LENGTH_LONG).show()
                previousState
            }
            else -> {
                Snackbar.make(rootView, R.string.main_error_basic, Snackbar.LENGTH_LONG).show()
                previousState
            }
        }
    }
}
package com.example.trubul.airpurrr.helper

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.trubul.airpurrr.R
import com.example.trubul.airpurrr.model.DetectorModel
import com.example.trubul.airpurrr.viewmodel.DetectorViewModel
import com.google.android.material.snackbar.Snackbar

class SwitchHelper(private val detectorViewModel: DetectorViewModel) {
    var state = false

    fun handleFanStates(value: DetectorModel, login: String, password: String, previousState: Boolean, rootView: SwipeRefreshLayout) {
        when (value.workstate) {
            "WorkStates.Sleeping" -> {
                Snackbar.make(rootView, R.string.main_message_switch_processing, Snackbar.LENGTH_LONG).show()
                state = !previousState
                if (state) {
                    detectorViewModel.controlFan(true, login, password)
                } else {
                    detectorViewModel.controlFan(false, login, password)
                }
            }
            "WorkStates.Measuring" -> {
                Snackbar.make(rootView, R.string.main_message_error_measuring, Snackbar.LENGTH_LONG).show()
                state = previousState
            }
            else -> {
                Snackbar.make(rootView, R.string.main_message_error, Snackbar.LENGTH_LONG).show()
                state = previousState
            }
        }
    }
}
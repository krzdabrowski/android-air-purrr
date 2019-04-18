package com.example.trubul.airpurrr.helper

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.trubul.airpurrr.R
import com.example.trubul.airpurrr.model.DetectorModel
import com.example.trubul.airpurrr.viewmodel.DetectorViewModel
import com.google.android.material.snackbar.Snackbar

class PurifierHelper(private val detectorViewModel: DetectorViewModel) {
    var currentState = false

    fun handlePurifierStates(value: DetectorModel, login: String, password: String, previousState: Boolean, rootView: SwipeRefreshLayout) {
        when (value.workstate) {
            "WorkStates.Sleeping" -> {
                Snackbar.make(rootView, R.string.main_message_turn_on, Snackbar.LENGTH_LONG).show()
                currentState = !previousState
                detectorViewModel.controlFan(currentState, login, password)
            }
            "WorkStates.Measuring" -> {
                Snackbar.make(rootView, R.string.main_message_error_measuring, Snackbar.LENGTH_LONG).show()
                currentState = previousState
            }
            else -> {
                Snackbar.make(rootView, R.string.main_message_error, Snackbar.LENGTH_LONG).show()
                currentState = previousState
            }
        }
    }
}
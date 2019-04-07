package com.example.trubul.airpurrr.helper

import android.widget.CompoundButton
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.trubul.airpurrr.R
import com.example.trubul.airpurrr.model.DetectorModel
import com.example.trubul.airpurrr.viewmodel.DetectorViewModel
import com.google.android.material.snackbar.Snackbar

internal class SwitchHelper(private val detectorViewModel: DetectorViewModel) {

    internal var oldSwitchState = false

    internal fun handleFanStates(value: DetectorModel, switchView: CompoundButton, rootView: SwipeRefreshLayout, login: String, password: String, isChecked: Boolean) {
        when (value.workstate) {
            "WorkStates.Sleeping" -> {
                Snackbar.make(rootView, R.string.main_message_switch_processing, Snackbar.LENGTH_LONG).show()
                oldSwitchState = isChecked
                if (isChecked) {
                    detectorViewModel.controlFan(true, login, password)
                } else {
                    detectorViewModel.controlFan(false, login, password)
                }
            }
            "WorkStates.Measuring" -> {
                Snackbar.make(rootView, R.string.main_message_error_measuring, Snackbar.LENGTH_LONG).show()
                oldSwitchState = !isChecked
                switchView.isChecked = !isChecked
            }
            else -> {
                Snackbar.make(rootView, R.string.main_message_error, Snackbar.LENGTH_LONG).show()
                oldSwitchState = !isChecked
                switchView.isChecked = !isChecked
            }
        }
    }
}
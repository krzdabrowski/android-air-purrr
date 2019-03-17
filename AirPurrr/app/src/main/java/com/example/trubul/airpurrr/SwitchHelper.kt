package com.example.trubul.airpurrr

import android.view.View
import android.widget.CompoundButton
import com.google.android.material.snackbar.Snackbar

import java.util.concurrent.ExecutionException

private const val WORKSTATE_URL = "http://airpurrr.ga/workstate.txt"

internal class SwitchHelper(private val mParentLayout: View, private val mCallback: SwitchCallback) : CompoundButton.OnCheckedChangeListener {
    private var stateManual = false

    internal interface SwitchCallback {
        fun setSwitchManual(state: Boolean)
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        stateManual = isChecked
        controlRequests(stateManual)
    }

    private fun controlRequests(state: Boolean) {
        val workStates: String
        val switchOn = HttpsPostRequest()
        val switchOff = HttpsPostRequest()

        try {
            val getRequest = HttpGetRequest()
            workStates = getRequest.execute(WORKSTATE_URL).get()

            when (workStates) {
                "WorkStates.Sleeping\n" -> {
                    Snackbar.make(mParentLayout, R.string.main_message_switch_processing, Snackbar.LENGTH_LONG).show()
                    if (state) {  // send request if it was switch -> ON
                        switchOn.execute("MANUAL=1")  // it will be POST: req = params[0]

                    } else {
                        switchOff.execute("MANUAL=0")
                    }
                }
                "WorkStates.Measuring\n" -> {
                    Snackbar.make(mParentLayout, R.string.main_message_error_measuring, Snackbar.LENGTH_LONG).show()
                    keepState()
                }
                else -> {
                    Snackbar.make(mParentLayout, R.string.main_message_error, Snackbar.LENGTH_LONG).show()
                    keepState()
                }
            }
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: ExecutionException) {
            e.printStackTrace()
        } catch (e: NullPointerException) {
            Snackbar.make(mParentLayout, R.string.main_message_error_server, Snackbar.LENGTH_LONG).show()
            keepState()
        }

    }

    private fun keepState() {
        stateManual = !stateManual
        mCallback.setSwitchManual(stateManual)
    }
}
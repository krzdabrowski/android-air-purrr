package com.example.trubul.airpurrr.helper

import android.util.Base64
import android.view.View
import android.widget.CompoundButton
import com.example.trubul.airpurrr.R
import com.example.trubul.airpurrr.retrofit.DetectorService
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

internal class SwitchHelper(private val mParentLayout: View, private val hashedEmail: String,
                            private val hashedPassword: String, private val mCallback: SwitchCallback) : CompoundButton.OnCheckedChangeListener {

    private var stateManual = false
    var workStates = ""

    internal interface SwitchCallback {
        fun setSwitchManual(state: Boolean)
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        retrofitSwitch()
        stateManual = isChecked
    }

    private fun retrofitSwitch() {
        val service by lazy { DetectorService.createHttp() }

        CoroutineScope(Dispatchers.IO).launch {
            val request = service.getDetectorDataAsync()
            withContext(Dispatchers.Main) {
                try {
                    val response = request.await()
                    if (response.isSuccessful && response.body() != null) {
                        workStates = response.body()!!.workstate
                        controlRequests(stateManual)
                    } else {
                        workStates = "error"
                    }
                } catch (e: HttpException) {
                    workStates = "error"
                } catch (e: Throwable) {
                    workStates = "error"
                }
            }
        }
    }

    private fun controlRequests(state: Boolean) {
        try {
            when (workStates) {
                "WorkStates.Sleeping" -> {
                    Snackbar.make(mParentLayout, R.string.main_message_switch_processing, Snackbar.LENGTH_LONG).show()
                    if (state) {  // send request if it was switch -> ON
                        controlFan(true)
                    } else {
                        controlFan(false)
                    }
                }
                "WorkStates.Measuring" -> {
                    Snackbar.make(mParentLayout, R.string.main_message_error_measuring, Snackbar.LENGTH_LONG).show()
                    keepState()
                }
                else -> {
                    Snackbar.make(mParentLayout, R.string.main_message_error, Snackbar.LENGTH_LONG).show()
                    keepState()
                }
            }
        } catch (e: Throwable) {
            Snackbar.make(mParentLayout, R.string.main_message_error_server, Snackbar.LENGTH_LONG).show()
            keepState()
        }
    }

    private fun controlFan(turnOn: Boolean) {
        val service by lazy { DetectorService.createHttps() }

        CoroutineScope(Dispatchers.IO).launch {
            val request = if (turnOn) {
                service.controlFanAsync("Basic " + Base64.encodeToString("$hashedEmail:$hashedPassword".toByteArray(), Base64.NO_WRAP), "MANUAL=1")
            } else {
                service.controlFanAsync("Basic " + Base64.encodeToString("$hashedEmail:$hashedPassword".toByteArray(), Base64.NO_WRAP), "MANUAL=0")
            }
            withContext(Dispatchers.Main) {
                try {
                    request.await()
                } catch (e: HttpException) {
                    e.printStackTrace()
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun keepState() {
        mCallback.setSwitchManual(!stateManual)
        stateManual = !stateManual
    }
}
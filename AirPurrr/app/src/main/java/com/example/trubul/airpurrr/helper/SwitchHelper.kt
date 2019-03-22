package com.example.trubul.airpurrr.helper

import android.view.View
import android.widget.CompoundButton
import com.example.trubul.airpurrr.HttpsPostRequest
import com.example.trubul.airpurrr.R
import com.example.trubul.airpurrr.retrofit.DetectorService
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

import java.util.concurrent.ExecutionException

internal class SwitchHelper(private val mParentLayout: View, private val hashedEmail: String,
                            private val hashedPassword: String, private val mCallback: SwitchCallback) : CompoundButton.OnCheckedChangeListener {

    private var stateManual = false
    var workStates = ""

    internal interface SwitchCallback {
        fun setSwitchManual(state: Boolean)
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        stateManual = isChecked
        retrofitSwitch()
    }

    private fun controlRequests(state: Boolean) {
        val switchOn = HttpsPostRequest(hashedEmail, hashedPassword)
        val switchOff = HttpsPostRequest(hashedEmail, hashedPassword)

        try {
            when (workStates) {
                "WorkStates.Sleeping" -> {
                    Snackbar.make(mParentLayout, R.string.main_message_switch_processing, Snackbar.LENGTH_LONG).show()
                    if (state) {  // send request if it was switch -> ON
                        switchOn.execute("MANUAL=1")  // it will be POST: req = params[0]

                    } else {
                        switchOff.execute("MANUAL=0")
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

    private fun retrofitSwitch() {
        val service by lazy { DetectorService.create() }

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
}
package com.example.trubul.airpurrr

import android.view.View
import android.widget.CompoundButton
import com.example.trubul.airpurrr.model.Detector
import com.example.trubul.airpurrr.retrofit.DetectorService
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

    fun retrofitSwitch() {
        val service by lazy { DetectorService.create() }

        val call = service.getDetectorData()
        call.enqueue(object : Callback<Detector.Result> {
            override fun onResponse(call: Call<Detector.Result>, response: Response<Detector.Result>) {
                val data = response.body()
                if (data != null) {
                    workStates = data.workstate
                    controlRequests(stateManual)
                }
            }

            override fun onFailure(call: Call<Detector.Result>, t: Throwable) {
                workStates = "error"
            }
        })
    }
}
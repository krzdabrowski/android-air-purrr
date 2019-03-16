package com.example.trubul.airpurrr

import android.content.Context
import android.widget.CompoundButton
import android.widget.Toast

import java.util.concurrent.ExecutionException

internal class SwitchHelper(private val mContext: Context, private val mCallback: SwitchCallback) : CompoundButton.OnCheckedChangeListener {
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
                    Toast.makeText(mContext, R.string.main_message_switch_processing, Toast.LENGTH_LONG).show()
                    if (state) {  // send request if it was switch -> ON
                        switchOn.execute("MANUAL=1")  // it will be POST: req = params[0]

                    } else {
                        switchOff.execute("MANUAL=0")
                    }
                }
                "WorkStates.Measuring\n" -> {
                    Toast.makeText(mContext, R.string.main_message_error_measuring, Toast.LENGTH_LONG).show()
                    keepState()
                }
                else -> {
                    Toast.makeText(mContext, R.string.main_message_error.toString() + "NoWorkStates)", Toast.LENGTH_LONG).show()
                    keepState()
                }
            }
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: ExecutionException) {
            e.printStackTrace()
        } catch (e: NullPointerException) {
            Toast.makeText(mContext, mContext.getString(R.string.main_message_error_server) + "NullPointer)", Toast.LENGTH_LONG).show()
            keepState()
        }

    }

    private fun keepState() {
        stateManual = !stateManual
        mCallback.setSwitchManual(stateManual)
    }

    companion object {
        const val WORKSTATE_URL = "http://airpurrr.ga/workstate.txt"
    }

}
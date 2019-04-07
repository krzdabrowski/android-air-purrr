package com.example.trubul.airpurrr.helper

import android.util.Base64
import android.view.View
import android.widget.CompoundButton
import com.example.trubul.airpurrr.retrofit.DetectorDataService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

internal class SwitchHelper(private val mParentLayout: View, private val hashedEmail: String,
                            private val hashedPassword: String, private val mCallback: SwitchCallback) : CompoundButton.OnCheckedChangeListener {

    private var stateManual = false

    internal interface SwitchCallback {
        fun setSwitchManual(state: Boolean)
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
//        retrofitSwitch()
        stateManual = isChecked
    }


//    private fun controlFan(turnOn: Boolean) {
//        val service by lazy { DetectorDataService.createHttps() }
//
//        CoroutineScope(Dispatchers.IO).launch {
//            val request = if (turnOn) {
//                service.controlFanAsync("Basic " + Base64.encodeToString("$hashedEmail:$hashedPassword".toByteArray(), Base64.NO_WRAP), "MANUAL=1")
//            } else {
//                service.controlFanAsync("Basic " + Base64.encodeToString("$hashedEmail:$hashedPassword".toByteArray(), Base64.NO_WRAP), "MANUAL=0")
//            }
//            withContext(Dispatchers.Main) {
//                try {
//                    request.await()
//                } catch (e: HttpException) {
//                    e.printStackTrace()
//                } catch (e: Throwable) {
//                    e.printStackTrace()
//                }
//            }
//        }
//    }

}
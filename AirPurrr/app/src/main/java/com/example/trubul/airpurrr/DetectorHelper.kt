package com.example.trubul.airpurrr

import android.content.Context
import android.util.Log

import java.util.Arrays

import androidx.loader.content.AsyncTaskLoader

internal class DetectorHelper(callback: DetectorCallback) {

    internal interface DetectorCallback {
        fun setPMValuesDetector(pmValuesDetector: Array<Double>)
    }

    init {
        mCallback = callback
    }

    internal class Loader(context: Context) : AsyncTaskLoader<Array<Double>>(context) {
        override fun loadInBackground(): Array<Double>? {
            return download()
        }
    }

    companion object {
        private lateinit var mCallback: DetectorCallback

        fun download(): Array<Double> {
            val rawData: String?

            try {
                val getRequest = HttpGetRequest()
                rawData = getRequest.doHttpRequest(MainActivity.DETECTOR_URL)

                val pmStrings = rawData!!.split("\n".toRegex()).dropLastWhile { it.isEmpty() }
                var pmDoubles = arrayOf<Double>()

                for (i in pmStrings.indices) {
                    try {
                        pmDoubles[i] = pmStrings[i].toDouble()
                    } catch (e: NumberFormatException) { }
                }

                // Convert results to percentages (to ease handling with auto mode)
                pmDoubles = convertToPercent(pmDoubles)
                mCallback.setPMValuesDetector(pmDoubles)

                return pmDoubles
            } catch (e: NullPointerException) {
                val empty = arrayOf(0.0, 0.0)
                mCallback.setPMValuesDetector(empty)
                return empty
            }

        }

        private fun convertToPercent(pmDoubles: Array<Double>): Array<Double> {
            val pmDoublesPerc = arrayOf<Double>()

            pmDoublesPerc[0] = 4 * pmDoubles[0]  // PM2.5
            pmDoublesPerc[1] = 2 * pmDoubles[1]  // PM10

            return pmDoublesPerc
        }
    }
}
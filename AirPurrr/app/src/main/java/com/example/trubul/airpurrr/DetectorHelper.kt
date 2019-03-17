package com.example.trubul.airpurrr

import android.content.Context
import androidx.loader.content.AsyncTaskLoader

private const val DETECTOR_URL = "http://airpurrr.ga/pm_data.txt"

internal class DetectorHelper {

    class Loader(context: Context) : AsyncTaskLoader<MutableList<Double>>(context) {
        override fun loadInBackground(): MutableList<Double> {
            val helper = DetectorHelper()
            return helper.download()
        }
    }

    fun download(): MutableList<Double> {
        val rawData: String?

        try {
            val getRequest = HttpGetRequest()
            rawData = getRequest.doHttpRequest(DETECTOR_URL)

            val pmStrings = rawData!!.split("\n".toRegex()).dropLastWhile { it.isEmpty() }
            var pmDoubles = mutableListOf<Double>()

            for (i in pmStrings.indices) {
                try {
                    pmDoubles.add(pmStrings[i].toDouble())
                } catch (e: NumberFormatException) {
                }
            }

            // Convert results to percentages (to ease handling with auto mode)
            pmDoubles = convertToPercent(pmDoubles)

            return pmDoubles
        } catch (e: NullPointerException) {
            val empty = mutableListOf(0.0, 0.0)
            return empty
        }

    }

    private fun convertToPercent(pmDoubles: List<Double>): MutableList<Double> {
        val pmDoublesPerc = mutableListOf<Double>()

        pmDoublesPerc.add(4 * pmDoubles[0])  // PM2.5
        pmDoublesPerc.add(2 * pmDoubles[1])  // PM10

        return pmDoublesPerc
    }
}
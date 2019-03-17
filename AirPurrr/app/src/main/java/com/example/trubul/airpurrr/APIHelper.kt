package com.example.trubul.airpurrr

import android.content.Context

import androidx.loader.content.AsyncTaskLoader

import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber

import java.util.ArrayList

private const val STATION_DATA_URL = "http://api.gios.gov.pl/pjp-api/rest/data/getData"

internal class APIHelper {

    internal class PMLoader(context: Context) : AsyncTaskLoader<List<Any>>(context) {
        override fun loadInBackground(): List<Any>? {
            val helper = APIHelper()
            return helper.downloadPMValues(3731, 3730)
        }
    }

        private fun downloadPMValues(pm25Sensor: Int, pm10Sensor: Int): List<Any>? {
            var rawData: String?
            var pm25LatestStringDate = "" // i = 0
            var pm10LatestStringDate = "" // i = 1
            var pm25LatestStringValue = "" // i = 0
            var pm10LatestStringValue = "" // i = 1

            try {
                for (i in 0..1) {
                    val getRequest = HttpGetRequest()

                    rawData = if (i == 0) {
                        getRequest.doHttpRequest("$STATION_DATA_URL/$pm25Sensor")
                    } else {
                        getRequest.doHttpRequest("$STATION_DATA_URL/$pm10Sensor")
                    }

                    val jsonData = JSONObject(rawData)  // return python's {key: value} of the provided link
                    val itemsArray = jsonData.getJSONArray("values")  // return array of dicts from "values" value

                    for (j in itemsArray.length() - 1 downTo 0) {  // to load last not-null value (last current value)
                        val specificDate = itemsArray.getJSONObject(j)

                        val date = specificDate.getString("date")
                        val value = specificDate.getString("value")

                        if (value != "null") {
                            if (i == 0) {
                                pm25LatestStringValue = value
                                pm25LatestStringDate = date
                            } else {
                                pm10LatestStringValue = value
                                pm10LatestStringDate = date
                            }
                        }
                    }
                }

                // Convert string to Double
                val pm25LatestDoubleValue = pm25LatestStringValue.toDouble()
                val pm10LatestDoubleValue = pm10LatestStringValue.toDouble()

                // Create Double[] and String[]
                var pmDoubles = listOf(pm25LatestDoubleValue, pm10LatestDoubleValue)
                val pmDates = listOf(pm25LatestStringDate, pm10LatestStringDate)

                // Convert results to percentages (to ease handling with auto mode)
                pmDoubles = convertToPercent(pmDoubles)

                // Add to List of Objects
                val pmDoublesDates = ArrayList<Any>(2)
                pmDoublesDates.add(pmDoubles)
                pmDoublesDates.add(pmDates)

                return pmDoublesDates

            } catch (e: NullPointerException) {
                return setEmptyList()
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            return null
        }

        private fun convertToPercent(pmDoubles: List<Double>): List<Double> {
            val pmDoublesPerc = mutableListOf<Double>()

            pmDoublesPerc.add(4 * pmDoubles[0])  // PM2.5
            pmDoublesPerc.add(2 * pmDoubles[1])  // PM10

            return pmDoublesPerc
        }

        private fun setEmptyList(): List<Any> {
            val empty = ArrayList<Any>(2)
            val emptyDouble = arrayOf(0.0, 0.0)
            val emptyString = arrayOf("no data", "no data")

            empty.add(emptyDouble)
            empty.add(emptyString)

            return empty
        }

}
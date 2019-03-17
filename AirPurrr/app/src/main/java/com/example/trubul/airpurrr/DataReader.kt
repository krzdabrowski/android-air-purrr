package com.example.trubul.airpurrr

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

internal class DataReader {

    fun getData(streamReader: InputStreamReader): String? {
        var reader: BufferedReader? = null
        var inputLine: String? = null

        try {
            reader = BufferedReader(streamReader)
            val stringBuilder = StringBuilder()

            //Check if the line we are reading is not null
            while ({ inputLine = reader.readLine(); inputLine }() != null) {
                stringBuilder.append(inputLine).append('\n')
            }

            return stringBuilder.toString()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (reader != null) {
                try {
                    reader.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }
        return null
    }
}

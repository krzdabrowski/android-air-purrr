package com.example.trubul.airpurrr

import android.os.AsyncTask
import org.apache.http.params.CoreConnectionPNames.CONNECTION_TIMEOUT
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

private const val REQUESTED_METHOD = "GET"
private const val READ_TIMEOUT = 5000
private const val CONN_TIMEOUT = 3000

internal class HttpGetRequest : AsyncTask<String, Void, String>() {

    fun doHttpRequest(url: String): String? {
        var connection: HttpURLConnection? = null
        var streamReader: InputStreamReader? = null

        try {
            val myUrl = URL(url)
            connection = myUrl.openConnection() as HttpURLConnection

            connection.requestMethod = REQUESTED_METHOD
            connection.readTimeout = READ_TIMEOUT
            connection.connectTimeout = CONN_TIMEOUT

            connection.connect()

            streamReader = InputStreamReader(connection.inputStream)
            val dataReader = DataReader()

            return dataReader.getData(streamReader)

        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: SecurityException) {
            e.printStackTrace()
        } finally {
            connection?.disconnect()
            if (streamReader != null) {
                try {
                    streamReader.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

        return null
    }

    // AsyncTask version (Switch) of AsyncTaskLoader version (PMValues)
    override fun doInBackground(vararg strings: String): String? {
        return doHttpRequest(strings[0])
    }
}

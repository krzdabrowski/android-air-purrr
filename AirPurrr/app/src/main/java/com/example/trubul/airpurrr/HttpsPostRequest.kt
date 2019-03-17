package com.example.trubul.airpurrr

import android.os.AsyncTask
import android.util.Base64
import com.example.trubul.airpurrr.activity.MainActivity

import java.io.IOException
import java.io.InputStreamReader
import java.net.URL

import javax.net.ssl.HttpsURLConnection

private const val HTTPS_URL = "https://airpurrr.ga/login"
private const val REQUESTED_METHOD = "POST"
private const val READ_TIMEOUT = 7000
private const val CONN_TIMEOUT = 3000

internal class HttpsPostRequest(private val hashedEmail: String, private val hashedPassword: String) : AsyncTask<String, Void, String>() {

    override fun doInBackground(vararg params: String): String? {
        var streamReader: InputStreamReader? = null

        try {
            val url = URL(HTTPS_URL)
            val connection = url.openConnection() as HttpsURLConnection

            connection.readTimeout = READ_TIMEOUT  // time for anything, responses etc
            connection.connectTimeout = CONN_TIMEOUT  // time to connect with IP
            connection.requestMethod = REQUESTED_METHOD

            // Set username and login_password
            val hash = "$hashedEmail:$hashedPassword"
            val basicAuth = "Basic " + Base64.encodeToString(hash.toByteArray(), Base64.NO_WRAP)
            connection.setRequestProperty("Authorization", basicAuth)

            // Send POST with data in body (key:value in form-data)
            val str = "req=" + params[0]
            val outputInBytes = str.toByteArray(charset("UTF-8"))

            val os = connection.outputStream
            os.write(outputInBytes)
            os.close()
            connection.connect()

            // Create a new InputStreamReader to read output info from webserver
            streamReader = InputStreamReader(connection.inputStream)
            // Do the data-read
            val dataReader = DataReader()
            dataReader.getData(streamReader)

        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: SecurityException) {
            e.printStackTrace()
        } finally {
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
}

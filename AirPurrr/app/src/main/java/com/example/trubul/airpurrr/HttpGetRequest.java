package com.example.trubul.airpurrr;

import android.util.Log;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by krzysiek
 * On 3/3/18.
 */

class HttpGetRequest {
    private static final String TAG = "HttpGetRequest";
    private static final String REQUESTED_METHOD = "GET";
    private static final int READ_TIMEOUT = 5000;
    private static final int CONNECTION_TIMEOUT = 3000;


    // a.k.a. dawny doInBackground()
    String makeHttpRequest(String... params) {
//        Log.d(TAG, "START");
        HttpURLConnection connection = null;
        InputStreamReader streamReader = null;

        try {
            // Create a URL object holding our url
            URL myUrl = new URL(params[0]);

            // Create a connection
            connection = (HttpURLConnection) myUrl.openConnection();

            // Set methods and timeouts
            connection.setRequestMethod(REQUESTED_METHOD);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);

            // Connect to our url
            connection.connect();

            // Create a new InputStreamReader
            streamReader = new InputStreamReader(connection.getInputStream());
            // Do the data-read
            DataReader dataReader = new DataReader();

            return dataReader.getData(streamReader);

        } catch (MalformedURLException e) {
            Log.e(TAG, "doInBackground: Invalid URL " + e.getMessage());
        } catch (ProtocolException e) {
            Log.e(TAG, "doInBackground: Protocol Exception " + e.getMessage());
        } catch (SecurityException e) {
            Log.e(TAG, "doInBackground: Security Exception. Needs permission? " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "doInBackground: IO Exception reading data: " + e.getMessage());
        } finally {
            if(connection != null) {
                connection.disconnect();
            }
            if (streamReader != null) {
                try {
                    streamReader.close();
                } catch (IOException e) {
                    Log.e(TAG, "doInBackground: Error closing stream " + e.getMessage());
                }
            }
        }

        return null;
    }

}

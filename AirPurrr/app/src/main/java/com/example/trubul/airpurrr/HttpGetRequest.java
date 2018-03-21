package com.example.trubul.airpurrr;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by krzysiek
 * On 3/3/18.
 */

public class HttpGetRequest extends AsyncTask<String, Void, String> {

    private static final String TAG = "HttpGetRequest";
    private static final String REQUESTED_METHOD = "GET";
    private static final int READ_TIMEOUT = 3000;
    private static final int CONNECTION_TIMEOUT = 7000;
    private String mResult = null;


    @Override
    protected String doInBackground(String... params) {
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
            mResult = dataReader.getResult(streamReader);

        }
        catch (MalformedURLException e) {
            Log.e(TAG, "doInBackground: Invalid URL " + e.getMessage());
        }
        catch (ProtocolException e) {
            e.printStackTrace();
        }
        catch(SecurityException e) {
            Log.e(TAG, "doInBackground: Security Exception. Needs permission? " + e.getMessage());
        }
        catch (IOException e) {
            Log.e(TAG, "HttpGetRequest: IO Exception reading data: " + e.getMessage());
        }
        finally {
            if(connection != null) {
                connection.disconnect();
            }
            if(streamReader != null) {
                try {
                    streamReader.close();
                }
                catch(IOException e) {
                    Log.e(TAG, "doInBackground: Error closing stream " + e.getMessage());
                }
            }
        }

        Log.d(TAG, "HttpGetRequest result is: " + mResult);
        return mResult;
    }

}

package com.example.trubul.airpurrr;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by krzysiek
 * On 3/3/18.
 */

// a.k.a. old AbortableRequest
class HttpsPostRequest extends AsyncTask<String, Void, String> {
    private static final String TAG = "HttpsPostRequest";
    private static final String HTTPS_URL = "https://airpurrr.ga/login";
    private static final String REQUESTED_METHOD = "POST";
    private static final int READ_TIMEOUT = 7000;
    private static final int CONNECTION_TIMEOUT = 3000;


    HttpsPostRequest() { }

    @Override
    protected String doInBackground(String... params) {
//        Log.w(TAG, "START<-END");
        InputStreamReader streamReader = null;

        try {
            // Create a connection
            URL url = new URL(HTTPS_URL);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

            // Set timeout and method
            connection.setReadTimeout(READ_TIMEOUT);  // time for anything, responses etc
            connection.setConnectTimeout(CONNECTION_TIMEOUT);  // time to connect with IP
            connection.setRequestMethod(REQUESTED_METHOD);

            // Set username and login_password
            String hash = MainActivity.getHashedEmail() + ":" + MainActivity.getHashedPassword();
            String basicAuth = "Basic " + Base64.encodeToString(hash.getBytes(), Base64.NO_WRAP);
            connection.setRequestProperty("Authorization", basicAuth);

            // Send POST with data in body (key:value in form-data)
            String str = "req=" + params[0];
            byte[] outputInBytes = str.getBytes("UTF-8");

            OutputStream os = connection.getOutputStream();
            os.write(outputInBytes);
            os.close();
            connection.connect();

            Log.d(TAG, "doInBackground: CODE is " + connection.getResponseCode());

//            Log.w(TAG, "START->END");
            // Create a new InputStreamReader to read output info from webserver
            streamReader = new InputStreamReader(connection.getInputStream());
            // Do the data-read
            DataReader dataReader = new DataReader();
            dataReader.getData(streamReader);

        } catch (MalformedURLException e) {
            Log.e(TAG, "doInBackground: Invalid URL " + e.getMessage());
        } catch (ProtocolException e) {
            Log.e(TAG, "doInBackground: Protocol Exception " + e.getMessage());
        } catch(SecurityException e) {
            Log.e(TAG, "doInBackground: Security Exception. Needs permission? " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "doInBackground: IO Exception reading data: " + e.getMessage());
        } finally {
            if(streamReader != null) {
                try {
                    streamReader.close();
                } catch(IOException e) {
                    Log.e(TAG, "doInBackground: Error closing stream " + e.getMessage());
                }
            }
        }

        return null;
    }
}

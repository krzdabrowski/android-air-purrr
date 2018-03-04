package com.example.trubul.airpurrr;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by krzysiek on 3/3/18.
 */

public class HttpGetRequest extends AsyncTask<String, Void, String> {

    private static final String TAG = "HttpGetRequest";
    private static final String REQUESTED_METHOD = "GET";
    private static final int READ_TIMEOUT = 15000;
    private static final int CONNECTION_TIMEOUT = 15000;
    private String mResult = null;


    @Override
    protected String doInBackground(String... params) {

        try {
            //Create a URL object holding our url
            URL myUrl = new URL(params[0]);

            //Create a connection
            HttpURLConnection connection = (HttpURLConnection) myUrl.openConnection();

            //Set methods and timeouts
            connection.setRequestMethod(REQUESTED_METHOD);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);

            //Connect to our url
            connection.connect();

            //Create a new InputStreamReader
            InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
            //Do the data-read
            DataReader dataReader = new DataReader();
            mResult = dataReader.getResult(streamReader);

        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (ProtocolException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "HttpGetRequest result is: " + mResult);
        return mResult;
    }

}

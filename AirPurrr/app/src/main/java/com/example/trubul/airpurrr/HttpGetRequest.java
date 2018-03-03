package com.example.trubul.airpurrr;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by krzysiek on 3/3/18.
 */

public class HttpGetRequest extends AsyncTask<String, Void, String> {

    public static final String REQUESTED_METHOD = "GET";
    public static final int READ_TIMEOUT = 15000;
    public static final int CONNECTION_TIMEOUT = 15000;

    @Override
    protected String doInBackground(String... params) {
        String stringUrl = params[0];
        String result;
        String inputLine;

        try {
            //Create a URL object holding our url
            URL myUrl = new URL(stringUrl);

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
            BufferedReader in = new BufferedReader(streamReader);

            //Create a new buffered reader and String Builder
            //BufferedReader reader = new BufferedReader(streamReader);
            StringBuilder stringBuilder = new StringBuilder();

            //Check if the line we are reading is not null
            while((inputLine = in.readLine()) != null) {
                stringBuilder.append(inputLine);
            }

            //Close our InputStream and Buffered reader
            in.close();
            streamReader.close();

            result = stringBuilder.toString();

        }
        catch (MalformedURLException e) {
            e.printStackTrace();
            result = "zle";
        }
        catch (IOException e) {
            e.printStackTrace();
            result = "zle";
        }

        return result;
    }

}

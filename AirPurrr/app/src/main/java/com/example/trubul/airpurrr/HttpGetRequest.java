package com.example.trubul.airpurrr;

import android.os.AsyncTask;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

class HttpGetRequest extends AsyncTask<String, Void, String> {
    private static final String REQUESTED_METHOD = "GET";
    private static final int READ_TIMEOUT = 5000;
    private static final int CONNECTION_TIMEOUT = 3000;

    String doHttpRequest(String url) {
        HttpURLConnection connection = null;
        InputStreamReader streamReader = null;

        try {
            URL myUrl = new URL(url);
            connection = (HttpURLConnection) myUrl.openConnection();

            connection.setRequestMethod(REQUESTED_METHOD);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);

            connection.connect();

            streamReader = new InputStreamReader(connection.getInputStream());
            DataReader dataReader = new DataReader();

            return dataReader.getData(streamReader);

        } catch (IOException | SecurityException e) {
            e.printStackTrace();
        } finally {
            if(connection != null) {
                connection.disconnect();
            }
            if (streamReader != null) {
                try {
                    streamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }


    // AsyncTask version (Switch) of AsyncTaskLoader version (PMValues)
    @Override
    protected String doInBackground(String... strings) {
        return doHttpRequest(strings[0]);
    }
}

package com.example.trubul.airpurrr;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by krzysiek on 3/3/18.
 */

public class AbortableRequest extends AsyncTask<String, Void, String>{

    private static final String TAG = "AbortableRequest";
    private HttpGet mRequest = new HttpGet();

    public AbortableRequest(HttpGet newRequest) {
            mRequest = newRequest;
        }

    public HttpGet getRequest() {
            return mRequest;
        }

    public void setRequest(HttpGet newRequest) {
            mRequest = newRequest;
        }

    @Override
    protected String doInBackground(String... params) {
        String result;
        try {
            URI url = new URI("http://192.168.0.248/?" + params[0]);
            getRequest().setURI(url);

            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = httpclient.execute(getRequest());

            InputStreamReader streamReader = new InputStreamReader(response.getEntity().getContent());

            DataReader dataReader = new DataReader();
            result = dataReader.getResult(streamReader);
            Log.d(TAG, "AbortableRequest result is: " + result);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return null;
    }

}

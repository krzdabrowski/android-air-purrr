package com.example.trubul.airpurrr;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.mklimek.sslutilsandroid.SslUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

/**
 * Created by krzysiek
 * On 3/3/18.
 */

// a.k.a. old AbortableRequest
class HttpsPostRequest extends AsyncTask<String, Void, String> {
    private static final String TAG = "HttpsPostRequest";
    private WeakReference<Context> contextRef;
    private static final String REQUESTED_METHOD = "POST";
    private static final int READ_TIMEOUT = 3000;
    private static final int CONNECTION_TIMEOUT = 1000;


    HttpsPostRequest(Context context) {
        contextRef = new WeakReference<>(context);
    }

    @Override
    protected String doInBackground(String... params) {
        Log.w(TAG, "START<-END");
        InputStreamReader streamReader = null;
        Context context = contextRef.get();

        try {
            // Create a connection
            URL url = new URL("https://89.70.85.249.nip.io:2137");
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

            // Set timeout and method
            connection.setReadTimeout(READ_TIMEOUT);  // time for anything, responses etc
            connection.setConnectTimeout(CONNECTION_TIMEOUT);  // time to connect with IP
            connection.setRequestMethod(REQUESTED_METHOD);

            // Set TLS and cert to my_IP_nip.io cert (it needs DNS to SSL hence nip.io)
            SSLContext.getInstance("TLS");
            SSLContext sc = SslUtils.getSslContextForCertificateFile(context, "apache-selfsigned-nipio.cer");
            connection.setSSLSocketFactory(sc.getSocketFactory());

            // Set username and password
            String userpass = MainActivity.getEmail() + ":" + MainActivity.getPassword();
            String basicAuth = "Basic " + Base64.encodeToString(userpass.getBytes(), Base64.DEFAULT);
            connection.setRequestProperty("Authorization", basicAuth);


            // Send POST data
            String str = "req=" + params[0];
            byte[] outputInBytes = str.getBytes("UTF-8");

            OutputStream os = connection.getOutputStream();
            os.write(outputInBytes);
            os.close();
            connection.connect();

            Log.w(TAG, "START->END");
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
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "doInBackground: No Such Algorithm Exception " + e.getMessage());
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

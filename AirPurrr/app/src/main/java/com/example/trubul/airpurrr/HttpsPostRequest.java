package com.example.trubul.airpurrr;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.mklimek.sslutilsandroid.SslUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;

/**
 * Created by krzysiek
 * On 3/21/18.
 */

public class HttpsPostRequest {

    private static final String TAG = "HttpsPostRequest";
    private static final String REQUESTED_METHOD = "POST";
    private static final int READ_TIMEOUT = 3000;
    private static final int CONNECTION_TIMEOUT = 7000;

    private static MyCallback mCallback;

    public HttpsPostRequest(MyCallback callback) {
        mCallback = callback;
    }

    public interface MyCallback {
        List<String> getEmailPassword();
    }

    public static HttpsURLConnection setRequest(Context context) {
        try {

            // Create a connection
            URL url = new URL("https://89.70.85.249:2137");
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

            // Set timeout and method
            connection.setReadTimeout(READ_TIMEOUT);  // czas na cala reszte, responsy itd
            connection.setConnectTimeout(CONNECTION_TIMEOUT);  // czas na polaczenie sie z IP
            connection.setRequestMethod(REQUESTED_METHOD);

            // Set default hostname verifier to null
            connection.setDefaultHostnameVerifier(new NullHostNameVerifier());

            // Create the TLS connection, set trust manager to be null and cert to mycert1024
            SSLContext sc;
            sc = SSLContext.getInstance("TLS");
            sc.init(null, new X509TrustManager[]{new NullX509TrustManager()}, new SecureRandom());
            sc = SslUtils.getSslContextForCertificateFile(context, "mycert1024.cer");
            connection.setSSLSocketFactory(sc.getSocketFactory());
            connection.setDefaultSSLSocketFactory(sc.getSocketFactory());


            // TLS authentication
//            String userpass = email + ":" + password;
//            String userpass = mCallback.getEmailPassword().get(0) + ":" + mCallback.getEmailPassword().get(1);
//            String basicAuth = "Basic " + Base64.encodeToString(userpass.getBytes(), Base64.DEFAULT);
//            connection.setRequestProperty("Authorization", basicAuth);

//            connection.setDoInput(true);

            return connection;

        }

        catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "sdds " + e.getMessage());
        }
        catch (KeyManagementException e) {
            Log.e(TAG, "doInBackground: Invalid URL " + e.getMessage());
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
            Log.e(TAG, "doInBackground: IO Exception reading data: " + e.getMessage());
        }

        return null;
    }

    public static HttpsURLConnection finishSetRequest(HttpsURLConnection conn) {
        String userpass = mCallback.getEmailPassword().get(0) + ":" + mCallback.getEmailPassword().get(1);
        String basicAuth = "Basic " + Base64.encodeToString(userpass.getBytes(), Base64.DEFAULT);
        conn.setRequestProperty("Authorization", basicAuth);

        return conn;
    }


}

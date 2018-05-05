package com.example.trubul.airpurrr;

import android.content.Context;
import android.util.Base64;
import android.util.Log;
import com.mklimek.sslutilsandroid.SslUtils;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

/**
 * Created by krzysiek
 * On 3/21/18.
 */

public class HttpsPostRequest {

    private static final String TAG = "HttpsPostRequest";
    private static final String REQUESTED_METHOD = "POST";
    private static final int READ_TIMEOUT = 5000;
//    private static final int READ_TIMEOUT = 1;  // to force SSLHandshake
    private static final int CONNECTION_TIMEOUT = 7000;
    private static MyCallback mCallback;


    public HttpsPostRequest(MyCallback callback) {
        mCallback = callback;
    }

    public interface MyCallback {
        String[] getEmailPassword();
    }


    public static HttpsURLConnection setRequest(Context context) {
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
            String userpass = mCallback.getEmailPassword()[0] + ":" + mCallback.getEmailPassword()[1];
            String basicAuth = "Basic " + Base64.encodeToString(userpass.getBytes(), Base64.DEFAULT);
            connection.setRequestProperty("Authorization", basicAuth);

            return connection;
        }

        catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "sdds " + e.getMessage());
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

}

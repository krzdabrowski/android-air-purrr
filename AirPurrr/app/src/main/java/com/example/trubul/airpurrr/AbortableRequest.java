package com.example.trubul.airpurrr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.mklimek.sslutilsandroid.SslUtils;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;


/**
 * Created by krzysiek
 * On 3/3/18.
 */

public class AbortableRequest extends AsyncTask<String, Void, String> {

    private static final String TAG = "AbortableRequest";
//    private HttpGet mRequest = new HttpGet();
//    private HttpPost mRequest = new HttpPost();
    private OkHttpClient mOkHttpClient;
    private static Context mContext;
    private boolean flagSSLHandshake = false;

//    public AbortableRequest(HttpPost newRequest, Context context) {
//            mRequest = newRequest;
//            this.mContext = context;
//        }



    public AbortableRequest(Context context) {
        this.mContext = context;
    }

//    private HttpPost getRequest() {
//            return mRequest;
//        }

    private OkHttpClient getHttpClient() {
        return mOkHttpClient;
    }


//    private InputStream getInputStream(String urlStr, String user, String password) throws IOException
//    {
//
//        }
//        catch (NoSuchAlgorithmException e) {
//            Log.e(TAG, "sdds " + e.getMessage());
//        }
//        catch (KeyManagementException e) {
//            Log.e(TAG, "doInBackground: Invalid URL " + e.getMessage());
//        }
//
//        return null;
//
//    }





    @Override
    protected String doInBackground(String... params) {
        String result;
        InputStreamReader streamReader = null;


        try {
//            URI url = new URI("http://192.168.0.248/?" + params[0]);


//            URL url = new URL("https://89.70.85.249:2137");
//            List<String> data = LoginActivity.sendPOST();
//
////            URL url = new URL(urlStr);
//            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
//            conn.setDefaultHostnameVerifier(new NullHostNameVerifier());
//
//            // Create the SSL connection
//            SSLContext sc;
//            sc = SSLContext.getInstance("TLS");
//            sc.init(null, new X509TrustManager[]{new NullX509TrustManager()}, new SecureRandom());
//
//            sc = SslUtils.getSslContextForCertificateFile(mContext, "mycert1024.cer");
//            conn.setSSLSocketFactory(sc.getSocketFactory());
//            conn.setDefaultSSLSocketFactory(sc.getSocketFactory());
//
//            Log.d(TAG, "PO SSL SOCKECIE");
//
//
//            // Use this if you need SSL authentication
//            String userpass = data.get(0) + ":" + data.get(1);
//            String basicAuth = "Basic " + Base64.encodeToString(userpass.getBytes(), Base64.DEFAULT);
//            conn.setRequestProperty("Authorization", basicAuth);
//            Log.d(TAG, "PO BASIC AUTH");



            HttpsURLConnection conn = LoginActivity.doMagic(mContext);





            // Add any data you wish to post here

            Log.d(TAG, "PRZED STR");
            String str = "req=" + params[0];

            Log.d(TAG, "PRZED BYTE[]");
            byte[] outputInBytes = str.getBytes("UTF-8");

            Log.d(TAG, "PRZED OS");
            OutputStream os = conn.getOutputStream();

            Log.d(TAG, "PRZED WRITE");
            os.write( outputInBytes );

//            os.flush();

            Log.d(TAG, "PRZED ZAMKNIECIEM");
            os.close();

            Log.d(TAG, "PO OUTPUTSTREAMIE, PRZED NAWIAZANIEM POLACZENIA");



            conn.connect();
            Log.d(TAG, "PO NAWIAZANIU POLACZENIA");

//            Log.d(TAG, "getInputStream is: " + conn.getInputStream());
//            return conn.getInputStream();




//            RequestBody formBody = LoginActivity.sendPOST();

//            RequestBody formBody = new FormEncodingBuilder()
//                    .add("login", data.get(0))
//                    .add("password", data.get(1))
//                    .add("req", params[0])
//                    .build();

//            Request request = new Request.Builder()
//                    .url(url)
//                    .post(formBody)
//                    .build();


//            OkHttpClient client = new OkHttpClient();



//            HttpClient httpclient = new DefaultHttpClient();
//            HttpResponse response = httpclient.execute(request);

            streamReader = new InputStreamReader(conn.getInputStream());
//            streamReader = new InputStreamReader(response.body().byteStream());
//            streamReader = new InputStreamReader(response.getEntity().getContent());

            DataReader dataReader = new DataReader();
            result = dataReader.getResult(streamReader);
            Log.d(TAG, "AbortableRequest result is: " + result);

        }
//        catch (URISyntaxException e) {
//            e.printStackTrace();
//        }

//        catch (NoSuchAlgorithmException e) {
//            Log.e(TAG, "sdds " + e.getMessage());
//        }
//        catch (KeyManagementException e) {
//            Log.e(TAG, "doInBackground: Invalid URL " + e.getMessage());
//        }
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
            if (!e.getMessage().equals("timeout")) { // timeout == there was a connection and it's while(true)'ing == good
                flagSSLHandshake = true;
            }
//            Toast.makeText(context, "Serwer nie odp, spróbuj ponownie później", Toast.LENGTH_LONG).show();
//            mCallback.setSwitchAuto(false);
        }
        finally {
            if(streamReader != null) {
                try {
                    streamReader.close();
                }
                catch(IOException e) {
                    Log.e(TAG, "doInBackground: Error closing stream " + e.getMessage());
                }

            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        if (flagSSLHandshake) {
            Toast.makeText(mContext, "BLAD SERWERAAAA", Toast.LENGTH_LONG).show();
//            if (SwitchListener.getMode().equals(SwitchListener.WorkingMode.AUTO)) {
//                mCallback.setSwitchAuto(!SwitchListener.isState());
//            }
//            else {
//                mCallback.setSwitchManual(!SwitchListener.isState());
//            }
            MainActivity.getAutoListener().keepState();
            MainActivity.getManualListener().keepState();

        }
    }
}


package com.example.trubul.airpurrr;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.ProtocolException;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by krzysiek
 * On 3/3/18.
 */

public class AbortableRequest extends AsyncTask<String, Void, String> {

    private static final String TAG = "AbortableRequest";
    private WeakReference<Context> contextRef;  // to use Toast
    private boolean flagSSLHandshake = false;


    public AbortableRequest(Context context) {
        contextRef = new WeakReference<>(context);
    }


    @Override
    protected String doInBackground(String... params) {
        InputStreamReader streamReader = null;
        Context context = contextRef.get();

        try {
            HttpsURLConnection conn = HttpsPostRequest.setRequest(context);

            // Send POST data
            String str = "req=" + params[0];
            byte[] outputInBytes = str.getBytes("UTF-8");

            OutputStream os = conn.getOutputStream();
            os.write(outputInBytes);
            os.close();
            conn.connect();

            // Create a new InputStreamReader to read output info from webserver
            streamReader = new InputStreamReader(conn.getInputStream());
            // Do the data-read
            DataReader dataReader = new DataReader();
            dataReader.getResult(streamReader);
//            Log.d(TAG, "doInBackground: READ from site" + xxx);

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
            if (!e.getMessage().equals("timeout")) { // timeout = there was a connection and it's while(true)'ing = good
                flagSSLHandshake = true;
            }
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
        Context context = contextRef.get();

        if (flagSSLHandshake) {
            Toast.makeText(context, "Błąd SSL handshake'a", Toast.LENGTH_LONG).show();

            if (MainActivity.getAutoListener().isLastUseAuto()) {
                MainActivity.getAutoListener().keepState();
            }
            else if (MainActivity.getManualListener().isLastUseManual()) {
                MainActivity.getManualListener().keepState();
            }

            flagSSLHandshake = false;
        }
    }
}

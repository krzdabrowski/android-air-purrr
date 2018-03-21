package com.example.trubul.airpurrr;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import javax.net.ssl.HttpsURLConnection;

/**
 * Created by krzysiek
 * On 3/3/18.
 */

public class AbortableRequest extends AsyncTask<String, Void, String> {

    private static final String TAG = "AbortableRequest";
    private Context mContext;
    private boolean flagSSLHandshake = false;


    public AbortableRequest(Context context) {
        this.mContext = context;
    }


    @Override
    protected String doInBackground(String... params) {
        InputStreamReader streamReader = null;

        try {
            HttpsURLConnection conn = LoginActivity.doMagic(mContext);

            // Add any data you wish to post here
            String str = "req=" + params[0];
            byte[] outputInBytes = str.getBytes("UTF-8");

            OutputStream os = conn.getOutputStream();
            os.write( outputInBytes );
            os.close();
            conn.connect();

            streamReader = new InputStreamReader(conn.getInputStream());
            DataReader dataReader = new DataReader();
            dataReader.getResult(streamReader);

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
            if (!e.getMessage().equals("timeout")) { // timeout == there was a connection and it's while(true)'ing == good
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
        if (flagSSLHandshake) {
            Toast.makeText(mContext, "Błąd SSL handshake'a", Toast.LENGTH_LONG).show();

            if (MainActivity.getAutoListener().isFlagLastUseAuto()) {
                MainActivity.getAutoListener().keepState();
            }
            else if (MainActivity.getManualListener().isFlagLastUseManual()) {
                MainActivity.getManualListener().keepState();
            }

            flagSSLHandshake = false;
        }
    }
}

package com.example.trubul.airpurrr;

import android.os.AsyncTask;
import android.util.Log;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by krzysiek
 * On 3/3/18.
 */

public class AbortableRequest extends AsyncTask<String, Void, String>{

    private static final String TAG = "AbortableRequest";
    private HttpGet mRequest = new HttpGet();

    public AbortableRequest(HttpGet newRequest) {
            mRequest = newRequest;
        }

    private HttpGet getRequest() {
            return mRequest;
        }

    @Override
    protected String doInBackground(String... params) {
        String result;
        InputStreamReader streamReader = null;

        try {
            URI url = new URI("http://192.168.0.248/?" + params[0]);
//            URI url = new URI("http://xxx.xxx.xxx.xxx:xxx/?" + params[0]);
            getRequest().setURI(url);

            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = httpclient.execute(getRequest());

            streamReader = new InputStreamReader(response.getEntity().getContent());

            DataReader dataReader = new DataReader();
            result = dataReader.getResult(streamReader);
            Log.d(TAG, "AbortableRequest result is: " + result);

        }
        catch (URISyntaxException e) {
            e.printStackTrace();
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

}

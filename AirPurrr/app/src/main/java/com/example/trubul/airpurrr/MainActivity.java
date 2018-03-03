package com.example.trubul.airpurrr;

import android.app.Activity;
import android.app.Application;
import android.net.http.HttpResponseCache;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    boolean flagToggle1 = true;  // zawsze musi byc true
    boolean flagToggle2 = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        workaround na android.os.NetworkOnMainThreadException
//        if (android.os.Build.VERSION.SDK_INT > 9)
//        {
//            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//            StrictMode.setThreadPolicy(policy);
//        }

        // ALE LEPIEJ ZROBIC NA ASYNCTASKU JAK NA MEDIUM JEST TUTORIAL CO I JAK DOKLADNIE!!!

        Switch switch_auto = findViewById(R.id.switch_auto);
        Switch switch_manual = findViewById(R.id.switch_manual);


        switch_auto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            HttpGet requestOn = new HttpGet();
            HttpGet requestOff = new HttpGet();
            String myUrl = "http://192.168.0.248/workstate.txt";
            String res;

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (!flagToggle1) {
                    requestOn = new HttpGet();
                    requestOff = new HttpGet();
                    flagToggle1 = true;
                }

                Background_get switchOn = new Background_get(requestOn);
                Background_get switchOff = new Background_get(requestOff);

                if (isChecked) {

                    try {
                        HttpGetRequest getRek = new HttpGetRequest();

                        res = getRek.execute(myUrl).get();
                        Log.d(TAG, "resulted is: " + res);

                        if (res.equals("WorkStates.Sleeping")) {
                            Toast.makeText(getApplicationContext(), "Śpię, wiec jestem", Toast.LENGTH_LONG).show();
                        }
                        else if (res.equals("WorkStates.Measuring")) {
                            Toast.makeText(getApplicationContext(), "Ale proszę nie przeszkadzać na BOGA", Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Zgłupiałem", Toast.LENGTH_LONG).show();
                        }

                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    //switchOn.execute("led1=1");


                }
                else {
//                    requestOn.abort();  // zerwij petle while w pracy wentylatora
//                    switchOff.execute("led1=0");
//                    flagToggle1 = false;

                    try {
                        HttpGetRequest getRek = new HttpGetRequest();

                        res = getRek.execute(myUrl).get();
                        Log.d(TAG, "resulted is: " + res);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    catch (ExecutionException e) {
                        e.printStackTrace();
                    }

                }

            }
        });

        switch_manual.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            HttpGet requestOn = new HttpGet();
            HttpGet requestOff = new HttpGet();

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (!flagToggle2) {
                    requestOn = new HttpGet();
                    requestOff = new HttpGet();
                    flagToggle2 = true;
                }

                Background_get switchOn = new Background_get(requestOn);
                Background_get switchOff = new Background_get(requestOff);

                if (isChecked) {
                    switchOn.execute("led2=1");
                }
                else {
                    requestOn.abort();  // zerwij petle while w pracy wentylatora
                    switchOff.execute("led2=0");
                    flagToggle2 = false;
                }

            }
        });
    }



    public class Background_get extends AsyncTask<String, Void, String> {

        private HttpGet mRequest = new HttpGet();

        public Background_get(HttpGet newRequest) {
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
            try {
                URI url = new URI("http://192.168.0.248/?" + params[0]);
                // HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                HttpClient httpclient = new DefaultHttpClient();
//                HttpGet request = new HttpGet(url);
                getRequest().setURI(url);
                HttpResponse response = httpclient.execute(getRequest());

                // BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                StringBuilder result = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null)
                    result.append(inputLine).append("\n");

                in.close();
<<<<<<< HEAD
                // response.close();
                // httpclient.close();

=======
>>>>>>> 62c46c7... Add HttpGetRequest functionality to get data from RPi
                return result.toString();

            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

            return null;
        }
    }







//    class MyRequest extends Background_get {
//
//        private HttpGet mRequest;
//
//        public HttpGet getGlobalVarValue() {
//            return mRequest;
//        }
//
//        public void setGlobalVarValue(HttpGet str) {
//            mRequest = str;
//        }
//    }

}
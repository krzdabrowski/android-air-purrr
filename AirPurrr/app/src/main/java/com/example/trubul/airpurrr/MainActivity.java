package com.example.trubul.airpurrr;

import android.app.Notification;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static Switch mSwitchAuto;
    private static Switch mSwitchManual;




    public static Switch getSwitchAuto() {
        return mSwitchAuto;
    }
    public static Switch getSwitchManual() {
        return mSwitchManual;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

<<<<<<< HEAD
        Switch switch_auto = findViewById(R.id.switch_auto);
        final Switch switch_manual = findViewById(R.id.switch_manual);


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

                AbortableRequest switchOn = new AbortableRequest(requestOn);
                AbortableRequest switchOff = new AbortableRequest(requestOff);

                if (isChecked) {

                    try {
                        HttpGetRequest getRequest = new HttpGetRequest();
                        res = getRequest.execute(myUrl).get();

                        if (res.equals("WorkStates.Sleeping")) {
                            Toast.makeText(getApplicationContext(), "Przetwarzam żądanie...", Toast.LENGTH_LONG).show();
                            switchOn.execute("led1=1");
                        }
                        else if (res.equals("WorkStates.Measuring")) {
                            Toast.makeText(getApplicationContext(), "Nie mogę przetworzyć żądania - czujnik w trybie pomiarowym", Toast.LENGTH_LONG).show();
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
                }
                else {
                    Toast.makeText(getApplicationContext(), "Przetwarzam żądanie...", Toast.LENGTH_LONG).show();
                    requestOn.abort();  // zerwij petle while w pracy wentylatora
                    switchOff.execute("led1=0");
                    flagToggle1 = false;
                }

            }
        });





        switch_manual.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            HttpGet requestOn = new HttpGet();
            HttpGet requestOff = new HttpGet();
            String myUrl = "http://192.168.0.248/workstate.txt";
            String res;

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (!flagToggle2) {
                    requestOn = new HttpGet();
                    requestOff = new HttpGet();
                    flagToggle2 = true;
                }

                AbortableRequest switchOn = new AbortableRequest(requestOn);
                AbortableRequest switchOff = new AbortableRequest(requestOff);

                if (isChecked) {

                    try {
                        HttpGetRequest getRequest = new HttpGetRequest();
                        res = getRequest.execute(myUrl).get();
                        Log.d(TAG, "res is: " + res);

                        if (res.equals("WorkStates.Sleeping\n")) {
                            Toast.makeText(getApplicationContext(), "Przetwarzam żądanie...", Toast.LENGTH_LONG).show();
                            switchOn.execute("led2=1");
                        }
                        else if (res.equals("WorkStates.Measuring\n")) {
                            Toast.makeText(getApplicationContext(), "Nie mogę przetworzyć żądania - czujnik w trybie pomiarowym" , Toast.LENGTH_LONG).show();
                            switch_manual.setChecked(false);
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Coś się popsuło i nie było mnie słychać", Toast.LENGTH_LONG).show();
                        }

                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }


                else {
                    requestOn.abort();  // zerwij petle while w pracy wentylatora
                    switchOff.execute("led2=0"); // to (czy to?) nie powinno dac sie odpalic gdy jest measuring
                    flagToggle2 = false;

                    try {
                        HttpGetRequest getRequest = new HttpGetRequest();
                        res = getRequest.execute(myUrl).get();
//
                        if (res.equals("WorkStates.Sleeping\n")) {
                            Toast.makeText(getApplicationContext(), "Próbuję przetworzyć żądanie, proszę czekać...", Toast.LENGTH_SHORT).show();
                        }
                        else if (res.equals("WorkStates.Measuring\n")) {
                            Toast.makeText(getApplicationContext(), "Nie mogę przetworzyć żądania - czujnik w trybie pomiarowym" , Toast.LENGTH_LONG).show();
                            switch_manual.setChecked(true);
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Coś się popsuło i nie było mnie słychać", Toast.LENGTH_LONG).show();
                            switch_manual.setChecked(true);
                        }
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    catch (ExecutionException e) {
                        e.printStackTrace();
                    }

<<<<<<< HEAD
                in.close();
<<<<<<< HEAD
                // response.close();
                // httpclient.close();

=======
>>>>>>> 62c46c7... Add HttpGetRequest functionality to get data from RPi
                return result.toString();
=======
                }
>>>>>>> a8825b1... Encapsulation and functionality separation

            }
        });
    }
=======
        mSwitchAuto = findViewById(R.id.switch_auto);
        mSwitchManual = findViewById(R.id.switch_manual);
>>>>>>> bb5fb91... Encapsulation and functionality separation part 2

        SwitchListener autoListener = new SwitchListener(MainActivity.this, SwitchListener.WorkingMode.AUTO);
        mSwitchAuto.setOnCheckedChangeListener(autoListener);
        SwitchListener manualListener = new SwitchListener(MainActivity.this, SwitchListener.WorkingMode.MANUAL);
        mSwitchManual.setOnCheckedChangeListener(manualListener);

        ///////////////////////////////////////////////////////////////

        TextView pm25Data = findViewById(R.id.PM25_data);
        TextView pm10Data = findViewById(R.id.PM10_data);
        TextView tempData;
        int multiplier;

        PMData pmData = new PMData();
        Double[] pmResult = pmData.getPMData();
        //Double[] pmResult = {58.3, 192.7};

        pm25Data.setText(String.valueOf(pmResult[0]));
        pm10Data.setText(String.valueOf(pmResult[1]));

        for(int i=0; i<2; i++) {
            if (i == 0) {
                tempData = pm25Data;
                multiplier = 1;
            }
            else {
                tempData = pm10Data;
                multiplier = 2;
            }

            if (pmResult[i] > 0 && pmResult[i] <= 12.5 * multiplier) {
                tempData.setBackgroundResource(R.drawable.green_color);
            }
            else if (pmResult[i] > 12.5 * multiplier && pmResult[i] <= 25 * multiplier) {
                tempData.setBackgroundResource(R.drawable.lime_color);
            }
            else if (pmResult[i] > 25 * multiplier && pmResult[i] <= 50 * multiplier) {
                tempData.setBackgroundResource(R.drawable.yellow_color);
            }
            else if (pmResult[i] > 50 * multiplier && pmResult[i] <= 100 * multiplier) {
                tempData.setBackgroundResource(R.drawable.red_color);
            }
        }




    }

}
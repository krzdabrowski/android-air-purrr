package com.example.trubul.airpurrr;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity implements SwitchListener.MyCallback, SwipeListener.MyCallback, PMDataResults.MyCallback, AlertDialogForAuto.MyCallback {

    private static final String TAG = "MainActivity";
    public static boolean flagDetectorAPI = false; // false = DetectorMode, true = APIMode

    @BindView(R.id.switch_auto) Switch mSwitchAuto;
    @BindView(R.id.switch_manual) Switch mSwitchManual;
    @BindView(R.id.PM25_data_perc) TextView pm25DataPerc;
    @BindView(R.id.PM10_data_perc) TextView pm10DataPerc;
    @BindView(R.id.PM25_data_ugm3) TextView pm25DataUgm3;
    @BindView(R.id.PM10_data_ugm3) TextView pm10DataUgm3;
    @BindView(R.id.PM25_mode) TextView pm25Mode;
    @BindView(R.id.PM10_mode) TextView pm10Mode;
    @BindView(R.id.swipe_refresh) SwipeRefreshLayout mySwipeRefreshLayout;

    private PMDataDetector pmDataDetector = new PMDataDetector();
    private PMDataAPI pmDataAPI = new PMDataAPI();

    private PMDataResults pmDataDetectorResults = new PMDataResults(this);
    private PMDataResults pmDataAPIResults = new PMDataResults(this);

    private Double[] pmValuesDetector;
    private Double[] pmValuesAPI;
    private String[] pmDatesAPI;


    @Override
    public void setSwitchAuto(boolean keepState) {
        mSwitchAuto.setChecked(keepState);
    }

    @Override
    public void setSwitchManual(boolean keepState) {
        mSwitchManual.setChecked(keepState);
    }

    @Override // 100% = 25ug/m3
    public void setPM25DataPerc(Double[] pmValues) {
        pm25DataPerc.setText(getString(R.string.pm25_data_perc, 4 * pmValues[0]));
    }

    @Override // 100% = 50ug/m3
    public void setPM10DataPerc(Double[] pmValues) {
        pm10DataPerc.setText(getString(R.string.pm10_data_perc, 2 * pmValues[1]));
    }

    @Override
    public void setPM25DataUgm3(Double[] pmValues) {
        pm25DataUgm3.setText(getString(R.string.pm25_data_ugm3, pmValues[0]));
    }

    @Override
    public void setPM10DataUgm3(Double[] pmValues) {
        pm10DataUgm3.setText(getString(R.string.pm25_data_ugm3, pmValues[1]));
    }

    @Override
    public void setPM25Mode(String mode) {
        pm25Mode.setText(mode);
    }

    @Override
    public void setPM10Mode(String mode) {
        pm10Mode.setText(mode);
    }

    @Override
    public void setSwipeRefreshing(boolean state) { mySwipeRefreshLayout.setRefreshing(false); }

    @Override
    public TextView getPM25DataPerc() {
        return pm25DataPerc;
    }

    @Override
    public TextView getPM10DataPerc() {
        return pm10DataPerc;
    }

    @Override
    public PMDataDetector getPMDataDetector() {
        return pmDataDetector;
    }

    @Override
    public PMDataAPI getPMDataAPI() {
        return pmDataAPI;
    }

    @Override
    public PMDataResults getPMDataDetectorResults() {
        return pmDataDetectorResults;
    }

    @Override
    public PMDataResults getPMDataAPIResults() {
        return pmDataAPIResults;
    }


    public Double[] getPMValuesDetector() { return pmValuesDetector; }

    @Override
    public Double[] getPMValuesAPI() {
        return pmValuesAPI;
    }

    @Override
    public String[] getPMDatesAPI() {
        return pmDatesAPI;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

<<<<<<< HEAD
<<<<<<< HEAD


<<<<<<< HEAD
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
<<<<<<< HEAD
>>>>>>> bb5fb91... Encapsulation and functionality separation part 2
=======
        pm25DataPerc = findViewById(R.id.PM25_data_perc);
        pm10DataPerc = findViewById(R.id.PM10_data_perc);
        pm25DataUgm3 = findViewById(R.id.PM25_data_ugm3);
        pm10DataUgm3 = findViewById(R.id.PM10_data_ugm3);
        mySwipeRefreshLayout = findViewById(R.id.swipe_refresh);
=======
//        mSwitchManual = findViewById(R.id.switch_manual);
//        pm25DataPerc = findViewById(R.id.PM25_data_perc);
//        pm10DataPerc = findViewById(R.id.PM10_data_perc);
//        pm25DataUgm3 = findViewById(R.id.PM25_data_ugm3);
//        pm10DataUgm3 = findViewById(R.id.PM10_data_ugm3);
//        mySwipeRefreshLayout = findViewById(R.id.swipe_refresh);
>>>>>>> ca502df... Add butterknife handling & refactor all findViewById static fields

=======
>>>>>>> abb85a0... Clean up code
        //Double[] pmValues = {58.3, 92.7};
        pmValues = pmData.downloadPMData();
        pmData.showResults(pmValues);
=======
        //Double[] pmValuesDetector = {58.3, 92.7};
        pmValuesDetector = pmDataDetector.downloadPMDataDetector();
<<<<<<< HEAD
        pmDataDetector.showResults(pmValuesDetector);
>>>>>>> f0db366... Refactor PMData etc. to PMDataDetector
=======
        List<Object> pmValuesAndDatesAPI = pmDataAPI.downloadPMDataAPI(); // pobierz wartosci z List<Object> = {Double[], String[]}
        pmValuesAPI = (Double[]) pmValuesAndDatesAPI.get(0);
        pmDatesAPI = (String[]) pmValuesAndDatesAPI.get(1);

        pmDataDetectorResults.showResults(pmValuesDetector, null); // domyslnie pokaz wartosci z detektora
>>>>>>> f276198... Add support to smogAPI from the closest GIOS station

        ///////////////////////////////////////////////////////////////
>>>>>>> 87fbcba... Add working automatic mode, percentages and some minor fixes

        SwitchListener autoListener = new SwitchListener(this, SwitchListener.WorkingMode.AUTO, this);
        mSwitchAuto.setOnCheckedChangeListener(autoListener);
        SwitchListener manualListener = new SwitchListener(this, SwitchListener.WorkingMode.MANUAL, this);
        mSwitchManual.setOnCheckedChangeListener(manualListener);

        ///////////////////////////////////////////////////////////////

        mySwipeRefreshLayout.setOnRefreshListener(new SwipeListener(this));

        ///////////////////////////////////////////////////////////////

        View.OnClickListener textViewListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!flagDetectorAPI) {
                    flagDetectorAPI = true;
                    pmDataAPIResults.showResults(pmValuesAPI, pmDatesAPI);
                } else {
                    flagDetectorAPI = false;
                    pmDataDetectorResults.showResults(pmValuesDetector, null);
                }
            }
        };

        pm25DataPerc.setOnClickListener(textViewListener);
        pm10DataPerc.setOnClickListener(textViewListener);

    }

    //////////////////////////  MENU  ///////////////////////////////

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.control_locally: // TODO po 16)
                return true;
            case R.id.control_remotely: // TODO po 16)
                return true;
            case R.id.set_auto_threshold:
                new AlertDialogForAuto(this, this);
                return true;
            case R.id.refresh_detector:
                mySwipeRefreshLayout.setRefreshing(true);
                SwipeListener refreshDetectorListener = new SwipeListener(this);
                refreshDetectorListener.onRefreshDetector();
                return true;
            case R.id.refresh_api:
                mySwipeRefreshLayout.setRefreshing(true);
                SwipeListener refreshAPIListener = new SwipeListener(this);
                refreshAPIListener.onRefreshAPI();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
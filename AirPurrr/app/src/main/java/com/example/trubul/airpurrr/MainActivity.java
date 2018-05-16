package com.example.trubul.airpurrr;

import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements // SwipeListener.SwipeCallback,
        SwitchListener.SwitchCallback, Detector.DetectorCallback, API.APICallback,
        LoaderManager.LoaderCallbacks, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "MainActivity";
    static final String DETECTOR_URL = "http://89.70.85.249:2138/pm_data.txt";
    private static final String STATE_DETECTOR_PM25 = "DetectorPM25";
    private static final String STATE_DETECTOR_PM10 = "DetectorPM10";
    private static final String STATE_API_PM25 = "APIPM25";
    private static final String STATE_API_PM10 = "APIPM10";
    private static final String STATE_API_DATES = "APIDates";
    private static final String STATE_THRESHOLD = "Threshold";
    private static final String STATE_FLAGTRISTATEAUTO = "FlagTriStateAuto";

    private static final int LOADER_DETECTOR = 1;
    private static final int LOADER_API = 2;

    static boolean flagDetectorAPI = false;  // false = DetectorMode, true = APIMode
    static int flagTriStateAuto = 0;

    @BindView(R.id.switch_auto) Switch switchAuto;
    @BindView(R.id.switch_manual) Switch switchManual;
    @BindView(R.id.PM25_data) TextView pm25Data;
    @BindView(R.id.PM10_data) TextView pm10Data;
    @BindView(R.id.PM25_data_ugm3) TextView pm25DataUgm3;
    @BindView(R.id.PM10_data_ugm3) TextView pm10DataUgm3;
    @BindView(R.id.PM25_mode) TextView pm25Mode;
    @BindView(R.id.PM10_mode) TextView pm10Mode;
    @BindView(R.id.swipe_refresh) SwipeRefreshLayout mySwipeRefreshLayout;

    private static Bundle emailAndPassword;

    private Detector detector = new Detector(this);
    private API api = new API(this);  // must-be instance to make mCallback work

    // Downloaded PM values
    private Double[] pmValuesDetector = {0.0, 0.0};
    private List<Object> pmValuesAndDatesAPI;
    private Double[] pmValuesAPI = {0.0, 0.0};
    private String[] pmDatesAPI = {"date1", "date2"};

    private AlertDialogForAuto alertDialog = new AlertDialogForAuto(this);
    private int threshold = 100;

    private SwitchListener autoListener;
    private SwitchListener manualListener;


    /////////////////////////////////////  GETTERS & SETTERS  //////////////////////////////////////
    @Override
    public void setSwitchAuto(boolean state) {
        switchAuto.setChecked(state);
    }

    @Override
    public void setSwitchManual(boolean state) {
        switchManual.setChecked(state);
    }

    @Override
    public void setPMValuesAndDatesAPI(List<Object> pmValuesAndDatesAPI) {
        this.pmValuesAndDatesAPI = pmValuesAndDatesAPI;
    }

    @Override
    public void setPMValuesDetector(Double[] pmValuesDetector) {
        this.pmValuesDetector = pmValuesDetector;
    }

    private void setSwipeRefreshing(final boolean value) {
        mySwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mySwipeRefreshLayout.setRefreshing(value);
            }
        });
    }

    @Override
    public Double[] getPMValuesDetector() {
        return pmValuesDetector;
    }

    // Get login_email and login_password from LoginActivity
    static String getEmail() {
        return emailAndPassword.getString("login_email");
    }

    static String getPassword() {
        return emailAndPassword.getString("login_password");
    }

    // Update UI
    private void updateDetector() {
        flagDetectorAPI = false;
        setUI(pmValuesDetector, null);
    }

    private void updateAPI() {
        flagDetectorAPI = true;
        pmValuesAPI = (Double[]) pmValuesAndDatesAPI.get(0);
        pmDatesAPI = (String[]) pmValuesAndDatesAPI.get(1);
        setUI(pmValuesAPI, pmDatesAPI);
    }

    private void updateAutoMode() {
        if (pmValuesDetector[0] > threshold || pmValuesDetector[1] > threshold) {
            flagTriStateAuto = 2;
        } else {
            flagTriStateAuto = 1;
        }
    }

    private void automaticDownload() {
        Timer timer = new Timer();
        TimerTask minuteTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getLoaderManager().initLoader(LOADER_DETECTOR, null, MainActivity.this).forceLoad();
                        Log.d(TAG, "percentages are: " + Arrays.toString(getPMValuesDetector()));
                        Log.d(TAG, "runOnUiThread flagTriStateAuto is: " + flagTriStateAuto);
                    }
                });
            }
        };

        timer.schedule(minuteTask, 0, 1000 * 60);  // 1000*60*1 every 1 minute
    }

    //////////////////////////////////////////  ONCREATE  //////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
<<<<<<< HEAD
<<<<<<< HEAD

<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
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
=======
=======
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
=======
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
>>>>>>> b234e27... Add HTTPS POST support for controlling the fan (big mess, bug with 1st verification NOT fixed)
=======
//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);
>>>>>>> 1f6d3ea... Update gradle

<<<<<<< HEAD
<<<<<<< HEAD
>>>>>>> 355b373... Include LoginActivity with all visual widgets (no logic yet)
=======
=======
=======
>>>>>>> 393f4e9... Code refactor #4
        emailAndPassword = getIntent().getExtras();
>>>>>>> 26dbe62... Code refactor #2

<<<<<<< HEAD
>>>>>>> 2c6e5cf... Major refactoring, minor bug fixes & clean-up code
        // Download PM values from detector
        onNewDetectorData();
        detector.downloadAutomatically();  // download detector every 1 minute
=======
//        // Download PM values automatically from detector
=======
        emailAndPassword = getIntent().getExtras();  // get Email and Password from LoginActivity

        getLoaderManager().initLoader(LOADER_DETECTOR, null, this).forceLoad();  // Loader for Detector
        getLoaderManager().initLoader(LOADER_API, null, this).forceLoad();  // Loader for API
>>>>>>> f93a4fa... Implement AsyncTaskLoaders (part #2)
        automaticDownload();  // download Detector values every 1 minute
>>>>>>> 8ff772a... Implement AsyncTaskLoaders (part #1)

<<<<<<< HEAD

        // Default: show PM values from detector
//        setUI(pmValuesDetector, null);

<<<<<<< HEAD

>>>>>>> ef21956... Clean up comments and little fixes

=======
>>>>>>> 2c6e5cf... Major refactoring, minor bug fixes & clean-up code
=======
>>>>>>> f93a4fa... Implement AsyncTaskLoaders (part #2)
        /////////////////////// LISTENERS ///////////////////////
        autoListener = new SwitchListener(this, this, SwitchListener.WorkingMode.AUTO);
        switchAuto.setOnCheckedChangeListener(autoListener);
        manualListener = new SwitchListener(this, this, SwitchListener.WorkingMode.MANUAL);
        switchManual.setOnCheckedChangeListener(manualListener);
        mySwipeRefreshLayout.setOnRefreshListener(this);

        View.OnClickListener textViewListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flagDetectorAPI) {
                    updateDetector();
                } else {
                    updateAPI();
                }
            }
        };
        pm25Data.setOnClickListener(textViewListener);
        pm10Data.setOnClickListener(textViewListener);

        // ChangeListeners
        detector.setListener(new Detector.ChangeListener() {
            @Override
            public void onChange() {
                Log.d(TAG, "onChange detector: CHANGE");

                updateAutoMode();  // update auto mode flags = default threshold is 100%
                autoListener.autoMode(autoListener.stateAuto);  // control the fan
            }
        });

        alertDialog.setListener(new AlertDialogForAuto.ChangeListener() {
            @Override
            public void onChange() {
                Log.d(TAG, "onChange alertDialog: CHANGE");
                int getThreshold = alertDialog.getThreshold();
                if (getThreshold != 0) {
                    threshold = getThreshold;
                }
                Log.d(TAG, "THRESHOLD IS: " + threshold);

                updateAutoMode();  // update auto mode flags = default threshold is 100%
            }
        });
    }

    //////////////////////////////////////////  ROTATION  //////////////////////////////////////////
    @Override  // logic has to be BEFORE super() because it saves
    protected void onSaveInstanceState(Bundle outState) {
        outState.putDouble(STATE_DETECTOR_PM25, pmValuesDetector[0]);
        outState.putDouble(STATE_DETECTOR_PM10, pmValuesDetector[1]);
        outState.putDouble(STATE_API_PM25, pmValuesAPI[0]);
        outState.putDouble(STATE_API_PM10, pmValuesAPI[1]);
        outState.putStringArray(STATE_API_DATES, pmDatesAPI);
        if (threshold != 100) {
            outState.putInt(STATE_THRESHOLD, threshold);
        }
        outState.putInt(STATE_FLAGTRISTATEAUTO, flagTriStateAuto);
        super.onSaveInstanceState(outState);
    }

    @Override  // logic has to be AFTER super() because it restores
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        pmValuesDetector[0] = savedInstanceState.getDouble(STATE_DETECTOR_PM25);
        pmValuesDetector[1] = savedInstanceState.getDouble(STATE_DETECTOR_PM10);
        pmValuesAPI[0] = savedInstanceState.getDouble(STATE_API_PM25);
        pmValuesAPI[1] = savedInstanceState.getDouble(STATE_API_PM10);
        pmDatesAPI = savedInstanceState.getStringArray(STATE_API_DATES);
        threshold = savedInstanceState.getInt(STATE_THRESHOLD, threshold);
        flagTriStateAuto = savedInstanceState.getInt(STATE_FLAGTRISTATEAUTO);

        updateDetector();
    }

    //////////////////////////////////////////  LOADERS  ///////////////////////////////////////////
    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        if (id == LOADER_DETECTOR) {
            return new DetectorLoader(this);
        } else if (id == LOADER_API) {
            return new APILoader(this);
        }
        return null;
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public void onLoadFinished(Loader loader, Object data) {
        int id = loader.getId();

        if (id == LOADER_DETECTOR) {
            pmValuesDetector = (Double[]) data;
            updateDetector();
        } else if (id == LOADER_API) {
            pmValuesAndDatesAPI = (List<Object>) data;
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
    }

    ////////////////////////////////////////////  MENU  ////////////////////////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.set_auto_threshold:
                alertDialog.createDialog();
                return true;
            case R.id.refresh_detector:
                getLoaderManager().initLoader(LOADER_DETECTOR, null, this).forceLoad();
                setSwipeRefreshing(false);
                return true;
            case R.id.refresh_api:
                getLoaderManager().initLoader(LOADER_API, null, this).forceLoad();
                updateAPI();
                setSwipeRefreshing(false);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    ///////////////////////////////////////////  OTHERS  ///////////////////////////////////////////
    @Override
    public void onRefresh() {
        getLoaderManager().initLoader(LOADER_DETECTOR, null, this).forceLoad();
        getLoaderManager().initLoader(LOADER_API, null, this).forceLoad();
        setSwipeRefreshing(false);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);  // disable going back to the LoginActivity
    }


    private void setUI(Double[] pmValues, String[] pmDates) {
        TextView textView;

        // Set TextView colors
        for (int i = 0; i < 2; i++) {
            // First iteration = update PM2.5, second iteration = update PM10
            if (i == 0) {
                textView = pm25Data;
            } else {
                textView = pm10Data;
            }

            // Update colors
            if (pmValues[i] == 0) {  // connection error
                textView.setBackgroundResource(R.drawable.default_color);
                flagTriStateAuto = 0;
            } else if (pmValues[i] > 0 && pmValues[i] <= 50) {
                textView.setBackgroundResource(R.drawable.green_color);
            } else if (pmValues[i] > 50 && pmValues[i] <= 100) {
                textView.setBackgroundResource(R.drawable.lime_color);
            } else if (pmValues[i] > 100 && pmValues[i] <= 200) {
                textView.setBackgroundResource(R.drawable.yellow_color);
            } else {
                textView.setBackgroundResource(R.drawable.red_color);
            }
        }

        // Set TextView PM values
        pm25Data.setText(getString(R.string.UI_data_perc, pmValues[0]));
        pm10Data.setText(getString(R.string.UI_data_perc, pmValues[1]));
        pm25DataUgm3.setText(getString(R.string.UI_data_ugm3, pmValues[0] / 4));
        pm10DataUgm3.setText(getString(R.string.UI_data_ugm3, pmValues[1] / 2));

        // Set TextView mode
        if (!flagDetectorAPI) {  // if detector
            pm25Mode.setText(R.string.UI_indoors);
            pm10Mode.setText(R.string.UI_indoors);
        } else {  // if API
            if (pmDates != null) {
                pmDatesAPI = (String[]) pmValuesAndDatesAPI.get(1);
                pm25Mode.setText(getString(R.string.UI_api, pmDatesAPI[0]));
                pm10Mode.setText(getString(R.string.UI_api, pmDatesAPI[1]));
            }
        }
    }
}

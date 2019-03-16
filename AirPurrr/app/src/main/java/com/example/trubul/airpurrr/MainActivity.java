package com.example.trubul.airpurrr;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.annotation.NonNull;
import androidx.loader.app.LoaderManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.loader.content.Loader;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;

import com.example.trubul.airpurrr.databinding.ActivityMainBinding;

import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

// TODO: Timber library instead of log.x
// TODO: deal with every single deprecated library to use AndroidX version (or alternative other library -> for ex. ProgressDialog)
public class MainActivity extends AppCompatActivity implements // SwipeListener.SwipeCallback,
        SwitchHelper.SwitchCallback, DetectorHelper.DetectorCallback, APIHelper.APICallback,
        LoaderManager.LoaderCallbacks, SwipeRefreshLayout.OnRefreshListener {

    // TODO: implement good practices (https://github.com/ribot/android-guidelines/blob/master/project_and_code_guidelines.md)
    // TODO: (for future) implement TabLayout with current and predicted results/data in fragments & remove automatic switch (only manual left)
    // TODO: export these strings somewhere
    // TODO: Snackbars instead of Toasts
    private static final String TAG = "MainActivity";
    static final String DETECTOR_URL = "http://airpurrr.ga/pm_data.txt";

    private static final int LOADER_DETECTOR = 1;
    private static final int LOADER_API_PM = 2;

    static boolean flagDetectorAPI = false;  // false = DetectorMode, true = APIMode
    static int flagTriStateAuto = 0;

    // TODO: remove as much butterknife as possible & implement more .XML databinding
    @BindView(R.id.switch_manual) Switch switchManual;
    @BindView(R.id.swipe_refresh) SwipeRefreshLayout mySwipeRefreshLayout;

    private static SharedPreferences mSharedPreferences;

    // TODO: create model (Data: DetectorData, ApiData or so), viewmodel, view, helpers, ... packages
    private DetectorHelper detector = new DetectorHelper(this);
    private APIHelper api = new APIHelper(this);  // must-be instance to make mCallback work

    private Double[] pmValuesDetector;
    private List<Object> pmValuesAndDatesAPI;
    private Double[] pmValuesAPI;
    private String[] pmDatesAPI;
////    private List<List<Object>> stationLocations;
//    private Integer[] stationSensors;

    private CustomDialog alertDialog = new CustomDialog(this);
    private int threshold = 100;

    private SwitchHelper autoListener;
    private SwitchHelper manualListener;

    private ActivityMainBinding activityMainBinding;

    // TODO: consider to do something with getters/setters -> Observable variables?
//    @Override
//    public void setSwitchAuto(boolean state) {
//        switchAuto.setChecked(state);
//    }

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
        mySwipeRefreshLayout.post(() -> mySwipeRefreshLayout.setRefreshing(value));
    }

    @Override
    public Double[] getPMValuesDetector() {
        return pmValuesDetector;
    }

    // Get login_email and login_password from LoginActivity
    static String getHashedEmail() {
        return mSharedPreferences.getString(LoginActivity.SAVED_HASH_EMAIL_KEY, null);
    }

    static String getHashedPassword() {
        return mSharedPreferences.getString(LoginActivity.SAVED_HASH_PASSWORD_KEY, null);
    }

    // Update UI
    private void updateDetector() {
        flagDetectorAPI = false;
        setUI(pmValuesDetector);
    }

    private void updateAPI() {
        flagDetectorAPI = true;
        if (pmValuesAndDatesAPI != null) {
            pmValuesAPI = (Double[]) pmValuesAndDatesAPI.get(0);
            pmDatesAPI = (String[]) pmValuesAndDatesAPI.get(1);
        }
        setUI(pmValuesAPI);
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
                runOnUiThread(() -> {
                    LoaderManager.getInstance(MainActivity.this).initLoader(LOADER_DETECTOR, null, MainActivity.this).forceLoad();
                    Log.d(TAG, "percentages are: " + Arrays.toString(getPMValuesDetector()));
                    Log.d(TAG, "runOnUiThread flagTriStateAuto is: " + flagTriStateAuto);
                });
            }
        };

        timer.schedule(minuteTask, 0, 1000 * 60);  // 1000*60*1 every 1 minute
    }

    // TODO: re-design xml (ConstraintLayout is fine, but big tile of PM2.5/10 is a bad idea
    // TODO: -> create empty TextView with color and data will be shown in another TextView with gravity.CENTER or so
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
//        setContentView(R.layout.activity_main);
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
=======
=======
        startService(new Intent(this, LocationService.class));  // Location class
>>>>>>> 2dee706... Classes refactor and add LocationService
=======
>>>>>>> 546f0c9... Add finding closest GIOS station (not completed)
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
>>>>>>> 885e77f... First try of fingerprint authentication's implementation

        // TODO: xml-data-bind these
        pmValuesDetector = new Double[]{0.0, 0.0};
        pmValuesAPI = new Double[]{0.0, 0.0};
        pmDatesAPI = new String[]{getString(R.string.main_data_info_api_empty), getString(R.string.main_data_info_api_empty)};

<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
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
        getLoaderManager().initLoader(LOADER_DETECTOR, null, this).forceLoad();  // Loader for DetectorHelper
        getLoaderManager().initLoader(LOADER_API, null, this).forceLoad();  // Loader for APIHelper
        automaticDownload();  // download DetectorHelper values every 1 minute
>>>>>>> 2dee706... Classes refactor and add LocationService
=======
        getLoaderManager().initLoader(LOADER_DETECTOR, null, this).forceLoad();  // Loader for Detector PM data
        getLoaderManager().initLoader(LOADER_API_PM, null, this).forceLoad();  // Loader for API PM data
=======
        LoaderManager.getInstance(this).initLoader(LOADER_DETECTOR, null, this).forceLoad();  // Loader for Detector PM data
        LoaderManager.getInstance(this).initLoader(LOADER_API_PM, null, this).forceLoad();  // Loader for API PM data
>>>>>>> 97ce4c1... update codebase to Java 8 & update libraries
//        getLoaderManager().initLoader(LOADER_API_STATIONS, null, this).forceLoad();  // Loader for API Station Locations
        automaticDownload();  // downloadPMValues DetectorHelper values every 1 minute
>>>>>>> 0aba3d6... Add downloading station locations from API

<<<<<<< HEAD
=======
>>>>>>> 2c6e5cf... Major refactoring, minor bug fixes & clean-up code
=======
>>>>>>> f93a4fa... Implement AsyncTaskLoaders (part #2)
        /////////////////////// LISTENERS ///////////////////////
=======
>>>>>>> 206fe32... clean-up
        autoListener = new SwitchHelper(this, this, SwitchHelper.WorkingMode.AUTO);
//        switchAuto.setOnCheckedChangeListener();
        manualListener = new SwitchHelper(this, this, SwitchHelper.WorkingMode.MANUAL);
//        switchManual.setOnCheckedChangeListener(manualListener);
        mySwipeRefreshLayout.setOnRefreshListener(this);

//        activityMainBinding.switchAuto.setOnCheckedChangeListener(autoListener);
        activityMainBinding.switchManual.setOnCheckedChangeListener(manualListener);

        View.OnClickListener textViewListener = (view) -> {
            if (flagDetectorAPI) {
                updateDetector();
            } else {
                updateAPI();
            }
        };

//        pm25Data.setOnClickListener(textViewListener);
//        pm10Data.setOnClickListener(textViewListener);
        activityMainBinding.partialMainDataPm25.layoutMainData.setOnClickListener(textViewListener);
        activityMainBinding.partialMainDataPm10.layoutMainData.setOnClickListener(textViewListener);

        // ChangeListeners
        detector.setListener(() -> {
            Log.d(TAG, "onChange detector: CHANGE");

            updateAutoMode();  // update auto mode flags = default threshold is 100%
            autoListener.autoMode(autoListener.stateAuto);  // control the fan
        });

        alertDialog.setListener(() -> {
            Log.d(TAG, "onChange alertDialog: CHANGE");
            int getThreshold = alertDialog.getThreshold();
            if (getThreshold != 0) {
                threshold = getThreshold;
            }
            Log.d(TAG, "THRESHOLD IS: " + threshold);

            updateAutoMode();  // update auto mode flags = default threshold is 100%
        });
    }

    // TODO: remove loaders while implementing MVVM with LiveData
    @Override
    public @NonNull Loader onCreateLoader(int id, Bundle args) {
        if (id == LOADER_DETECTOR) {
            return new DetectorHelper.Loader(this);
        } else if (id == LOADER_API_PM) {
            return new APIHelper.PMLoader(this);
        }
        return null;
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public void onLoadFinished(@NonNull Loader loader, Object data) {
        int id = loader.getId();

        if (id == LOADER_DETECTOR) {
            pmValuesDetector = (Double[]) data;
            updateDetector();
        } else if (id == LOADER_API_PM) {
            pmValuesAndDatesAPI = (List<Object>) data;
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_threshold:
                alertDialog.createDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRefresh() {
        LoaderManager.getInstance(this).initLoader(LOADER_DETECTOR, null, this).forceLoad();
        LoaderManager.getInstance(this).initLoader(LOADER_API_PM, null, this).forceLoad();
        setSwipeRefreshing(false);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);  // disable going back to the LoginActivity
    }

    private void setUI(Double[] pmValues) {
        ConstraintLayout layout;

        // Set TextView colors
        for (int i = 0; i < 2; i++) {
            // First iteration = update PM2.5, second iteration = update PM10
            if (i == 0) {
                layout = activityMainBinding.partialMainDataPm25.layoutMainData;
            } else {
                layout = activityMainBinding.partialMainDataPm10.layoutMainData;
            }

            // Update colors
            if (pmValues[i] == 0) {  // connection error
                layout.setBackgroundResource(R.drawable.data_unavailable);
                flagTriStateAuto = 0;
            } else if (pmValues[i] > 0 && pmValues[i] <= 50) {
                layout.setBackgroundResource(R.drawable.data_green);
            } else if (pmValues[i] > 50 && pmValues[i] <= 100) {
                layout.setBackgroundResource(R.drawable.data_lime);
            } else if (pmValues[i] > 100 && pmValues[i] <= 200) {
                layout.setBackgroundResource(R.drawable.data_yellow);
            } else {
                layout.setBackgroundResource(R.drawable.data_red);
            }
        }

        // Set TextView PM values
        activityMainBinding.partialMainDataPm25.dataPercentage.setText(getString(R.string.main_data_percentage, pmValues[0]));
        activityMainBinding.partialMainDataPm10.dataPercentage.setText(getString(R.string.main_data_percentage, pmValues[1]));
        activityMainBinding.partialMainDataPm25.dataUgm3.setText(getString(R.string.main_data_ugm3, pmValues[0] / 4));
        activityMainBinding.partialMainDataPm10.dataUgm3.setText(getString(R.string.main_data_ugm3, pmValues[1] / 2));

        // Set TextView mode
        if (!flagDetectorAPI) {  // if detector
            activityMainBinding.partialMainDataPm25.dataSource.setText(R.string.main_data_info_indoors);
            activityMainBinding.partialMainDataPm10.dataSource.setText(R.string.main_data_info_indoors);
        } else {  // if APIHelper
            activityMainBinding.partialMainDataPm25.dataSource.setText(getString(R.string.main_data_info_api, pmDatesAPI[0]));
            activityMainBinding.partialMainDataPm10.dataSource.setText(getString(R.string.main_data_info_api, pmDatesAPI[1]));
        }
    }
}

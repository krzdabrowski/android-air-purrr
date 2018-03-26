package com.example.trubul.airpurrr;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.sql.Array;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

/**
 * Created by krzysiek
 * On 3/4/18.
 */

public class PMDataDetector {
    private static final String TAG = "PMDataDetector";
    private Context mContext;
    private MyCallback mCallback;
    private ChangeListener listener;
    private final Activity mActivity;

    private Double[] pmDoubles;


    public interface MyCallback {
        void setCurrentPMDetector(Double[] currentPMDetector);
        Double[] getCurrentPMDetector();
        PMDataResults getPMDataDetectorResults();
    }

    public interface ChangeListener {
        void onChange();
    }

    public PMDataDetector(Context context, MyCallback callback, Activity activity) {
        mContext = context;
        mCallback = callback;
        mActivity = activity;
    }

    public void setListener(ChangeListener listener) {
        this.listener = listener;
    }

    public Double[] downloadPMDataDetector(String pmDataDetectorURL) {
        String pmDataDetector;

        try {
            HttpGetRequest getRequest = new HttpGetRequest();
            pmDataDetector = getRequest.execute(pmDataDetectorURL).get();

            String[] pmStrings = pmDataDetector.split("\n");
            pmDoubles = new Double[pmStrings.length];

            for (int i = 0; i < pmStrings.length; i++) {
                try {
                    pmDoubles[i] = Double.parseDouble(pmStrings[i]);
                }
                catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

            // Convert results to percentages (to ease handling with auto mode)
            pmDoubles = convertToPercent(pmDoubles);

            // if values have changed
            if (!Arrays.equals(pmDoubles, mCallback.getCurrentPMDetector())) {
                mCallback.setCurrentPMDetector(pmDoubles);
                Log.d(TAG, "listener is: " + listener);
                listener.onChange();
            }

            // if not
            mCallback.setCurrentPMDetector(pmDoubles);
            return pmDoubles;
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        catch (ExecutionException e) {
            e.printStackTrace();
        }
        catch (NullPointerException e) {
            Double[] empty = {0.0, 0.0};
//            Toast.makeText(mContext, "Błąd połączenia z serwerem" , Toast.LENGTH_LONG).show();

            mCallback.setCurrentPMDetector(empty);
            return empty;
        }

        return null;
    }

    public void downloadAutoPMData() {
        Timer timer = new Timer();
        TimerTask minuteTask = new TimerTask() {
            @Override
            public void run() {

                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        downloadPMDataDetector(MainActivity.PM_DATA_DETECTOR_URL_REMOTE);
                        Log.d(TAG, "percentages are: " + java.util.Arrays.toString(mCallback.getCurrentPMDetector()));
//                        AlertDialogForAuto.setAutoThreshold()
                        Log.d(TAG, "runOnUiThread flagTriStateAuto is: " + mCallback.getPMDataDetectorResults().flagTriStateAuto);


                    }
                });

            }
        };

        // Schedule the task to run starting now and then every 1 minute
        // It works while screen is off and when app is in background!
        timer.schedule(minuteTask, 0, 1000*5);  // 1000*60*10 every 10 minute -> 1 min
    }


    public Double[] convertToPercent(Double[] pmDoubles) {
        Double[] pmDoublesPerc = new Double[2];

        pmDoublesPerc[0] = 4 * pmDoubles[0];  // PM2.5
        pmDoublesPerc[1] = 2 * pmDoubles[1];  // PM10

        return pmDoublesPerc;
    }

}

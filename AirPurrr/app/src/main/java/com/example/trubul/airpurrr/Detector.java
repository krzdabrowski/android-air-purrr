package com.example.trubul.airpurrr;

import android.app.Activity;
import android.util.Log;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

/**
 * Created by krzysiek
 * On 3/4/18.
 */

public class Detector {
    private static final String TAG = "Detector";
    private final Activity mActivity;
    private DetectorCallback mCallback;
    private ChangeListener listener;


    public interface DetectorCallback {
        Double[] getPMValuesDetector();
        void setPMValuesDetector(Double[] pmValuesDetector);
    }

    public interface ChangeListener {
        void onChange();
    }

    public void setListener(ChangeListener listener) {
        this.listener = listener;
    }

    public Detector(Activity activity, DetectorCallback callback) {
        mActivity = activity;
        mCallback = callback;
    }

    public Double[] download() {
        String rawData;

        try {
            HttpGetRequest getRequest = new HttpGetRequest();
            rawData = getRequest.execute(MainActivity.DETECTOR_URL).get();

            String[] pmStrings = rawData.split("\n");
            Double[] pmDoubles = new Double[pmStrings.length];

            for (int i = 0; i < pmStrings.length; i++) {
                try {
                    pmDoubles[i] = Double.parseDouble(pmStrings[i]);
                }
                catch (NumberFormatException e) {
                    Log.e(TAG, "Detector: Number Format Exception " + e.getMessage());
                }
            }

            // Convert results to percentages (to ease handling with auto mode)
            pmDoubles = convertToPercent(pmDoubles);

            // If values have changed
            if(!Arrays.equals(pmDoubles, mCallback.getPMValuesDetector())) {
                mCallback.setPMValuesDetector(pmDoubles);
                listener.onChange();
            }

            // If not
            mCallback.setPMValuesDetector(pmDoubles);
            return pmDoubles;
        }
        catch (InterruptedException e) {
            Log.e(TAG, "Detector: Interrupted Exception " + e.getMessage());
        }
        catch (ExecutionException e) {
            Log.e(TAG, "Detector: Execution Exception " + e.getMessage());
        }
        catch (NullPointerException e) {
            Double[] empty = {0.0, 0.0};
            mCallback.setPMValuesDetector(empty);
            return empty;
        }

        return null;
    }

    public void downloadAutomatically() {
        Timer timer = new Timer();
        TimerTask minuteTask = new TimerTask() {
            @Override
            public void run() {

                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        download();
                        Log.d(TAG, "percentages are: " + java.util.Arrays.toString(mCallback.getPMValuesDetector()));
                        Log.d(TAG, "runOnUiThread flagTriStateAuto is: " + MainActivity.flagTriStateAuto);
                    }
                });
            }
        };

        // Schedule the task to run starting now and then every 1 minute
        // It works while screen is off and when app is in background!
        timer.schedule(minuteTask, 0, 1000 * 60);  // 1000*60*1 every 1 minute
    }


    public Double[] convertToPercent(Double[] pmDoubles) {
        Double[] pmDoublesPerc = new Double[2];

        pmDoublesPerc[0] = 4 * pmDoubles[0];  // PM2.5
        pmDoublesPerc[1] = 2 * pmDoubles[1];  // PM10

        return pmDoublesPerc;
    }

}

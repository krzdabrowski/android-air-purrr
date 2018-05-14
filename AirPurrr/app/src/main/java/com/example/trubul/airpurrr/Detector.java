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

class Detector {
    private static final String TAG = "Detector";
    private final Activity mActivity;  // to make auto-download working
    private DetectorCallback mCallback;
    private ChangeListener listener;


    interface DetectorCallback {
        Double[] getPMValuesDetector();
        void setPMValuesDetector(Double[] pmValuesDetector);
    }

    interface ChangeListener {
        void onChange();
    }

    void setListener(ChangeListener listener) {
        this.listener = listener;
    }

    Detector(Activity activity, DetectorCallback callback) {
        mActivity = activity;
        mCallback = callback;
    }

    Double[] download() {
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
                    Log.e(TAG, "download: Number Format Exception " + e.getMessage());
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
            Log.e(TAG, "download: Interrupted Exception " + e.getMessage());
        }
        catch (ExecutionException e) {
            Log.e(TAG, "download: Execution Exception " + e.getMessage());
        }
        catch (NullPointerException e) {
            Double[] empty = {0.0, 0.0};
            mCallback.setPMValuesDetector(empty);
            return empty;
        }

        return null;
    }

    void downloadAutomatically() {
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


    private Double[] convertToPercent(Double[] pmDoubles) {
        Double[] pmDoublesPerc = new Double[2];

        pmDoublesPerc[0] = 4 * pmDoubles[0];  // PM2.5
        pmDoublesPerc[1] = 2 * pmDoubles[1];  // PM10

        return pmDoublesPerc;
    }

}

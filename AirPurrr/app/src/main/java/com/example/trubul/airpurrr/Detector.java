package com.example.trubul.airpurrr;

import android.app.Activity;
import android.content.Context;
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
    private Context mContext;
    private MyCallback mCallback;
    private ChangeListener listener;


    public interface MyCallback {
        Double[] getPMValuesDetector();
        void setPMValuesDetector(Double[] pmValuesDetector);

        TextViewResults getTextViewDetector();
    }

    public interface ChangeListener {
        void onChange();
    }

    public void setListener(ChangeListener listener) {
        this.listener = listener;
    }

    public Detector(Activity activity, Context context, MyCallback callback) {
        mActivity = activity;
        mContext = context;
        mCallback = callback;
    }


    public Double[] download(String detectorURL) {
        String rawData;

        try {
            HttpGetRequest getRequest = new HttpGetRequest();
            rawData = getRequest.execute(detectorURL).get();

            String[] pmStrings = rawData.split("\n");
            Double[] pmDoubles = new Double[pmStrings.length];

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

            // If values have changed
            if(!Arrays.equals(pmDoubles, mCallback.getPMValuesDetector())) {
                mCallback.setPMValuesDetector(pmDoubles);
                Log.d(TAG, "listener is: " + listener);
                listener.onChange();
            }

            // If not
            mCallback.setPMValuesDetector(pmDoubles);
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
                        download(MainActivity.DETECTOR_URL_REMOTE);
                        Log.d(TAG, "percentages are: " + java.util.Arrays.toString(mCallback.getPMValuesDetector()));
                        Log.d(TAG, "runOnUiThread flagTriStateAuto is: " + mCallback.getTextViewDetector().flagTriStateAuto);
                    }
                });

            }
        };

        // Schedule the task to run starting now and then every 1 minute
        // It works while screen is off and when app is in background!
        timer.schedule(minuteTask, 0, 1000*1);  // 1000*60*1 every 1 minute
    }


    public Double[] convertToPercent(Double[] pmDoubles) {
        Double[] pmDoublesPerc = new Double[2];

        pmDoublesPerc[0] = 4 * pmDoubles[0];  // PM2.5
        pmDoublesPerc[1] = 2 * pmDoubles[1];  // PM10

        return pmDoublesPerc;
    }

}

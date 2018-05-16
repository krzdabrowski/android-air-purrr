package com.example.trubul.airpurrr;

import android.app.Activity;
import android.util.Log;

import java.util.Arrays;

/**
 * Created by krzysiek
 * On 3/4/18.
 */

class Detector {
    private static final String TAG = "Detector";
    private final Activity mActivity;  // to make auto-download working
    private static DetectorCallback mCallback;
    private static ChangeListener listener;


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

    static Double[] download() {
        String rawData;

        try {
            HttpGetRequest getRequest = new HttpGetRequest();
            rawData = getRequest.makeHttpRequest(MainActivity.DETECTOR_URL);

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
//            if(!Arrays.equals(pmDoubles, mCallback.getPMValuesDetector())) {
//                mCallback.setPMValuesDetector(pmDoubles);
//                listener.onChange();
//            }

            // If not
            mCallback.setPMValuesDetector(pmDoubles);
            Log.d(TAG, "download: PMDOUBLES ARE" + Arrays.toString(pmDoubles));
            return pmDoubles;
        }
        catch (NullPointerException e) {
            Double[] empty = {0.0, 0.0};
            mCallback.setPMValuesDetector(empty);
            return empty;
        }
    }

    private static Double[] convertToPercent(Double[] pmDoubles) {
        Double[] pmDoublesPerc = new Double[2];

        pmDoublesPerc[0] = 4 * pmDoubles[0];  // PM2.5
        pmDoublesPerc[1] = 2 * pmDoubles[1];  // PM10

        return pmDoublesPerc;
    }

}

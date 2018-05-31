package com.example.trubul.airpurrr;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.Arrays;

/**
 * Created by krzysiek
 * On 3/4/18.
 */

class DetectorHelper {
    private static final String TAG = "DetectorHelper";
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

    DetectorHelper(DetectorCallback callback) {
        mCallback = callback;
    }

    static Double[] download() {
        String rawData;

        try {
            HttpGetRequest getRequest = new HttpGetRequest();
            rawData = getRequest.doHttpRequest(MainActivity.DETECTOR_URL);

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

            Log.d(TAG, "download: PMDOUBLES ARE " + Arrays.toString(pmDoubles));
            Log.d(TAG, "download: GETPMVALUESDETECTOR ARE " + Arrays.toString(mCallback.getPMValuesDetector()));

            // If values have changed
            if (!Arrays.equals(pmDoubles, mCallback.getPMValuesDetector())) {
                mCallback.setPMValuesDetector(pmDoubles);
                listener.onChange();
            }

            // If not
            mCallback.setPMValuesDetector(pmDoubles);

            return pmDoubles;
        } catch (NullPointerException e) {
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

    static class Loader extends AsyncTaskLoader<Double[]> {
        Loader(Context context) {
            super(context);
        }

        @Override
        public Double[] loadInBackground() {
            return DetectorHelper.download();
        }
    }
}
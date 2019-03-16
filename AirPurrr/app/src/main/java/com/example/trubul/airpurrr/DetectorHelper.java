package com.example.trubul.airpurrr;

import android.content.Context;
import android.util.Log;

import java.util.Arrays;

import androidx.loader.content.AsyncTaskLoader;

class DetectorHelper {
    private static final String TAG = "DetectorHelper";
    private static DetectorCallback mCallback;


    interface DetectorCallback {
        void setPMValuesDetector(Double[] pmValuesDetector);
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
                    Log.e(TAG, "downloadPMValues: Number Format Exception " + e.getMessage());
                }
            }

            // Convert results to percentages (to ease handling with auto mode)
            pmDoubles = convertToPercent(pmDoubles);

            Log.d(TAG, "downloadPMValues: PMDOUBLES ARE " + Arrays.toString(pmDoubles));
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
            return download();
        }
    }
}
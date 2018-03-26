package com.example.trubul.airpurrr;

import android.util.Log;
import android.widget.TextView;

/**
 * Created by krzysiek
 * On 3/14/18.
 */

public class PMDataResults {
    private static final String TAG = "PMDataResults";
    public int flagTriStateAuto = 0;
    int threshold = 100;
    private MyCallback mCallback;

//    private ChangeListener listener;
//
//    public interface ChangeListener {
//        void onChange();
//    }
//
//    public void setListener(ChangeListener listener) {
//        this.listener = listener;
//    }

    public interface MyCallback {
        TextView getPM25DataPerc();
        TextView getPM10DataPerc();

        void setPM25DataPerc(Double[] pmValues);
        void setPM10DataPerc(Double[] pmValues);
        void setPM25DataUgm3(Double[] pmValues);
        void setPM10DataUgm3(Double[] pmValues);
        void setPM25Mode(String mode);
        void setPM10Mode(String mode);

        Double[] getPMValuesAPI();
        String[] getPMDatesAPI();
    }

    public PMDataResults(MyCallback callback) {
        this.mCallback = callback;
    }

    public void showResults(Double[] pmValues, String[] pmDates) {
        TextView tempData;

        // Check if threshold has been set
        int getThreshold = AlertDialogForAuto.getThreshold();
        if (getThreshold != 0) {
            threshold = getThreshold;
        }
        Log.d(TAG, "THRESHOLD IS: " + threshold);


        for(int i=0; i<2; i++) {
            // First iteration = update PM2.5, second iteration = update PM10
            if (i == 0) {
                tempData = mCallback.getPM25DataPerc();
            }
            else {
                tempData = mCallback.getPM10DataPerc();
            }

            // Update colors
            if (pmValues [i] == 0) {  // blad polaczenia
                tempData.setBackgroundResource(R.drawable.default_color);
                flagTriStateAuto = 0;
            }
            else if (pmValues[i] > 0 && pmValues[i] <= 50) {
                tempData.setBackgroundResource(R.drawable.green_color);
            }
            else if (pmValues[i] > 50 && pmValues[i] <= 100) {
                tempData.setBackgroundResource(R.drawable.lime_color);
            }
            else if (pmValues[i] > 100 && pmValues[i] <= 200) {
                tempData.setBackgroundResource(R.drawable.yellow_color);
            }
            else {
                tempData.setBackgroundResource(R.drawable.red_color);
            }
        }

        mCallback.setPM25DataPerc(pmValues);
        mCallback.setPM10DataPerc(pmValues);
        mCallback.setPM25DataUgm3(pmValues);
        mCallback.setPM10DataUgm3(pmValues);

        if (!MainActivity.flagDetectorAPI) {  // if detector
            mCallback.setPM25Mode("W mieszkaniu");
            mCallback.setPM10Mode("W mieszkaniu");
        }
        else {  // if API
            mCallback.setPM25Mode("API z " + mCallback.getPMDatesAPI()[0]);
            mCallback.setPM10Mode("API z " + mCallback.getPMDatesAPI()[1]);
        }


        // Update flags = default threshold is 100%
        if (pmValues[0] > threshold || pmValues[1] > threshold) {
            flagTriStateAuto = 2;
        }
        else {
            flagTriStateAuto = 1;
        }

        MainActivity.getAutoListener().autoMode(MainActivity.getAutoListener().flagStateAuto);

    }

}

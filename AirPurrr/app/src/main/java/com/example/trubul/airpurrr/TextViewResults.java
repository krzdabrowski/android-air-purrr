package com.example.trubul.airpurrr;

import android.util.Log;
import android.widget.TextView;

/**
 * Created by krzysiek
 * On 3/14/18.
 */

public class TextViewResults {
    private static final String TAG = "TextViewResults";
    private MyCallback mCallback;
    public int flagTriStateAuto = 0;
    int threshold = 100;


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

    public void setFlagTriStateAuto(int flagTriStateAuto) {
        this.flagTriStateAuto = flagTriStateAuto;
    }

    public TextViewResults(MyCallback callback) {
        this.mCallback = callback;
    }


    public void showResults(Double[] pmValues, String[] pmDates) {
        TextView tempData;

        // Initial setting of flagTriState, default=100%
        if (pmValues[0] > threshold || pmValues[1] > threshold) {
            flagTriStateAuto = 2;
        }
        else {
            flagTriStateAuto = 1;
        }

        // Present results
        for(int i=0; i<2; i++) {
            // First iteration = update PM2.5, second iteration = update PM10
            if (i == 0) {
                tempData = mCallback.getPM25DataPerc();
            }
            else {
                tempData = mCallback.getPM10DataPerc();
            }

            // Update colors
            if (pmValues [i] == 0) {  // connection error
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

        // Set PM values in TextView
        mCallback.setPM25DataPerc(pmValues);
        mCallback.setPM10DataPerc(pmValues);
        mCallback.setPM25DataUgm3(pmValues);
        mCallback.setPM10DataUgm3(pmValues);

        Log.d(TAG, "SHOW RESULTS: " + mCallback.getPMDatesAPI()[0] + mCallback.getPMDatesAPI()[1]);

        // Set mode in TextView
        if (!MainActivity.flagDetectorAPI) {  // if detector
            mCallback.setPM25Mode("W mieszkaniu");
            mCallback.setPM10Mode("W mieszkaniu");
        }
        else {  // if API
            mCallback.setPM25Mode("API z " + mCallback.getPMDatesAPI()[0]);
            mCallback.setPM10Mode("API z " + mCallback.getPMDatesAPI()[1]);
        }

    }

}

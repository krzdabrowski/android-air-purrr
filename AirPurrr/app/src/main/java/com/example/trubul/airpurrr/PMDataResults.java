package com.example.trubul.airpurrr;

import android.widget.TextView;

/**
 * Created by krzysiek
 * On 3/14/18.
 */

public class PMDataResults {

    public int flagTriStateAuto = 0;
    private MyCallback mCallback;

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
        int multiplier;

        for(int i=0; i<2; i++) {
            if (i == 0) {
                tempData = mCallback.getPM25DataPerc();
                multiplier = 1;
            }
            else {
                tempData = mCallback.getPM10DataPerc();
                multiplier = 2;
            }
            if (pmValues [i] == 0) {  // blad polaczenia
                tempData.setBackgroundResource(R.drawable.default_color);
                flagTriStateAuto = 0;
            }
            else if (pmValues[i] > 0 && pmValues[i] <= 12.5 * multiplier) {
                tempData.setBackgroundResource(R.drawable.green_color);
                flagTriStateAuto = 1;
            }
            else if (pmValues[i] > 12.5 * multiplier && pmValues[i] <= 25 * multiplier) {
                tempData.setBackgroundResource(R.drawable.lime_color);
                flagTriStateAuto = 1;
            }
            else if (pmValues[i] > 25 * multiplier && pmValues[i] <= 50 * multiplier) {
                tempData.setBackgroundResource(R.drawable.yellow_color);
                flagTriStateAuto = 2;
            }
            else if (pmValues[i] > 50 * multiplier && pmValues[i] <= 100 * multiplier) {
                tempData.setBackgroundResource(R.drawable.red_color);
                flagTriStateAuto = 2;
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

    }

}

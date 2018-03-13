package com.example.trubul.airpurrr;

import android.widget.TextView;

import java.util.concurrent.ExecutionException;

/**
 * Created by krzysiek
 * On 3/4/18.
 */

public class PMDataDetector {

//    private static final String TAG = "PMDataDetector";
//    private static final String PM_DATA_DETECTOR_URL_GLOBAL = "http://xxx.xxx.xxx.xxx:xxx/pm_data.txt";
    private static final String PM_DATA_DETECTOR_URL = "http://192.168.0.248/pm_data.txt";
    public int flagTriStateAuto = 0;
    private MyCallback mCallback;

    public interface MyCallback {
        TextView getPM25DataDetectorPerc();
        TextView getPM10DataDetectorPerc();

        void setPM25DataPerc(Double[] pmValues); // set jest dla obu taki sam? - tj to samo TextView dla PMDataDetector i dla PMDataAPI
        void setPM10DataPerc(Double[] pmValues);
        void setPM25DataUgm3(Double[] pmValues);
        void setPM10DataUgm3(Double[] pmValues);
    }

    public PMDataDetector(MyCallback callback) {
        this.mCallback = callback;
    }


    public Double[] downloadPMDataDetector() {
        String pmDataDetector;
        try {
            HttpGetRequest getRequest = new HttpGetRequest();
            pmDataDetector = getRequest.execute(PM_DATA_DETECTOR_URL).get();

            String[] pmStrings = pmDataDetector.split("\n");
            Double[] pmDoubles = new Double[pmStrings.length];

            for (int i = 0; i < pmStrings.length; i++) {
                try {
                    pmDoubles[i] = Double.parseDouble(pmStrings[i]);
                }
                catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

            return pmDoubles;
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        catch (ExecutionException e) {
            e.printStackTrace();
        }
        catch (NullPointerException e) { // gdy np nie ma internetu
            return new Double[] {0.0, 0.0};
        }

        return null;
    }

    public void showResults(Double[] pmValues) {

        TextView tempData;
        int multiplier;

        for(int i=0; i<2; i++) {
            if (i == 0) {
                tempData = mCallback.getPM25DataDetectorPerc();
                multiplier = 1;
            }
            else {
                tempData = mCallback.getPM10DataDetectorPerc();
                multiplier = 2;
            }

            if (pmValues[i] > 0 && pmValues[i] <= 12.5 * multiplier) {
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

    }

}

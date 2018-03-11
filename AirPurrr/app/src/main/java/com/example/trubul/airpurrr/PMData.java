package com.example.trubul.airpurrr;

import android.widget.TextView;

import java.util.concurrent.ExecutionException;

/**
 * Created by krzysiek
 * On 3/4/18.
 */

public class PMData {

//    private static final String TAG = "PMData";
//    private static final String PM_DATA_URL_GLOBAL = "http://xxx.xxx.xxx.xxx:xxx/pm_data.txt";
    private static final String PM_DATA_URL = "http://192.168.0.248/pm_data.txt";
    public int flagTriStateAuto = 0;
    private MyCallback mCallback;

    public interface MyCallback {
        TextView getPM25DataPerc();
        TextView getPM10DataPerc();

        void setPM25DataPerc(Double[] pmValues);
        void setPM10DataPerc(Double[] pmValues);
        void setPM25DataUgm3(Double[] pmValues);
        void setPM10DataUgm3(Double[] pmValues);
    }

    public PMData(MyCallback callback) {
        this.mCallback = callback;
    }


    public Double[] downloadPMData() {
        String pmData;
        try {
            HttpGetRequest getRequest = new HttpGetRequest();
            pmData = getRequest.execute(PM_DATA_URL).get();

            String[] pmStrings = pmData.split("\n");
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
//
//        TextView pm25DataPerc;
//        TextView pm10DataPerc;
//        TextView pm25DataUgm3 = MainActivity.getPm25DataUgm3();
//        TextView pm10DataUgm3 = MainActivity.getPm10DataUgm3();


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

//        pm25DataPerc.setText(String.valueOf(4 * pmValues[0]) + "%"); // 100% = 25ug/m3
//        pm10DataPerc.setText(String.valueOf(2 * pmValues[1]) + "%"); // 100% = 50ug/m3
//        pm25DataUgm3.setText("(" + String.valueOf(pmValues[0]) + ")");
//        pm10DataUgm3.setText("(" + String.valueOf(pmValues[1]) + ")");
        mCallback.setPM25DataPerc(pmValues);
        mCallback.setPM10DataPerc(pmValues);
        mCallback.setPM25DataUgm3(pmValues);
        mCallback.setPM10DataUgm3(pmValues);



    }

}

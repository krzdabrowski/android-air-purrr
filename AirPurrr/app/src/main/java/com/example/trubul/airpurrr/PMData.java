package com.example.trubul.airpurrr;

import java.util.concurrent.ExecutionException;

/**
 * Created by krzysiek on 3/4/18.
 */

public class PMData {

    private static final String TAG = "PMData";
    private static final String PM_DATA_URL = "http://192.168.0.248/pm_data.txt";
    private String pm25_str = "0.0";
    private String pm10_str = "0.0";

    public Double[] getPMData() {
        String pmData;
        try {
            HttpGetRequest getRequest = new HttpGetRequest();
            pmData = getRequest.execute(PM_DATA_URL).get();

            String[] items = pmData.split("\n");
            Double[] pmResults = new Double[items.length];

            for (int i = 0; i < items.length; i++) {
                try {
                    pmResults[i] = Double.parseDouble(items[i]);
                }
                catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

            return pmResults;
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

}

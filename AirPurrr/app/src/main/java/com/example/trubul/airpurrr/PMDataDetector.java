package com.example.trubul.airpurrr;

import java.util.concurrent.ExecutionException;

/**
 * Created by krzysiek
 * On 3/4/18.
 */

public class PMDataDetector {

//    private static final String TAG = "PMDataDetector";
    private static final String PM_DATA_DETECTOR_URL = "http://89.70.85.249:2138/pm_data.txt";
//    private static final String PM_DATA_DETECTOR_URL = "http://192.168.0.248/pm_data.txt";

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

}

package com.example.trubul.airpurrr;

import android.content.res.Resources;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by krzysiek
 * On 3/13/18.
 */

class API {
    private static final String TAG = "API";
    private static final String PM25_API_URL = "http://api.gios.gov.pl/pjp-api/rest/data/getData/3731";
    private static final String PM10_API_URL = "http://api.gios.gov.pl/pjp-api/rest/data/getData/3730";
//    private static final String PM25_API_URL = "http://89.70.85.249:2138/testapi.txt";
//    private static final String PM10_API_URL = "http://89.70.85.249:2138/testapi.txt";
private static APICallback mCallback;


    interface APICallback {
        void setPMValuesAndDatesAPI(List<Object> pmValuesAndDatesAPI);
        }

    API(APICallback callback) {
        mCallback = callback;
    }

    static List<Object> download() {
        String pmDataAPI;
        String pm25LatestStringDate = null; // i = 0
        String pm10LatestStringDate = null; // i = 1
        String pm25LatestStringValue = null; // i = 0
        String pm10LatestStringValue = null; // i = 1

        try {
            for (int i=0; i<2; i++) {
                HttpGetRequest getRequest = new HttpGetRequest();

                if (i == 0) {
                    pmDataAPI = getRequest.doHttpRequest(PM25_API_URL);
                } else {
                    pmDataAPI = getRequest.doHttpRequest(PM10_API_URL);
                }

                JSONObject jsonData = new JSONObject(pmDataAPI);  // return python's {key: value} of the provided link
                JSONArray itemsArray = jsonData.getJSONArray("values");  // return array of dicts from "values" value

                for (int j = itemsArray.length() - 1; j >= 0; j--) {  // to load last not-null value (last current value)
                    JSONObject specificDict = itemsArray.getJSONObject(j);

                    String date = specificDict.getString("date");
                    String value = specificDict.getString("value");

                    if (!value.equals("null")) {
                        if (i == 0) {
                            pm25LatestStringValue = value;
                            pm25LatestStringDate = date;
                        }
                        else {
                            pm10LatestStringValue = value;
                            pm10LatestStringDate = date;
                        }
                    }
                }
            }

            // Convert string to Double
            Double pm25LatestDoubleValue = Double.parseDouble(pm25LatestStringValue);
            Double pm10LatestDoubleValue = Double.parseDouble(pm10LatestStringValue);

            // Create Double[] and String[]
            Double[] pmDoubles = new Double[]{ pm25LatestDoubleValue, pm10LatestDoubleValue } ;
            String[] pmDates = new String[]{ pm25LatestStringDate, pm10LatestStringDate };

            // Convert results to percentages (to ease handling with auto mode)
            pmDoubles = convertToPercent(pmDoubles);

            // Add to List of Objects
            List<Object> pmDoublesDates = new ArrayList<>(2);
            pmDoublesDates.add(pmDoubles);
            pmDoublesDates.add(pmDates);

            mCallback.setPMValuesAndDatesAPI(pmDoublesDates);
            return pmDoublesDates;

        } catch (NullPointerException e) {
            List<Object> empty = new ArrayList<>(2);
            Double[] emptyDouble = new Double[]{0.0, 0.0};
            String[] emptyString = new String[]{"no data", "no data"};

            empty.add(emptyDouble);
            empty.add(emptyString);

            mCallback.setPMValuesAndDatesAPI(empty);
            return empty;
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "download: Error processing JSON data " + e.getMessage());
        }

        return null;
    }

    private static Double[] convertToPercent(Double[] pmDoubles) {
        Double[] pmDoublesPerc = new Double[2];

        pmDoublesPerc[0] = 4 * pmDoubles[0];  // PM2.5
        pmDoublesPerc[1] = 2 * pmDoubles[1];  // PM10

        return pmDoublesPerc;
    }

}

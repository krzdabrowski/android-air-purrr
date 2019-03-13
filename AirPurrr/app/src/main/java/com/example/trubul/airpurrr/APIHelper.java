package com.example.trubul.airpurrr;

import android.content.Context;
import android.location.Location;

import androidx.loader.content.AsyncTaskLoader;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

class APIHelper {
    private static final String STATION_PM25_URL = "http://api.gios.gov.pl/pjp-api/rest/data/getData";
    private static final String STATION_PM10_URL = "http://api.gios.gov.pl/pjp-api/rest/data/getData";

    private static APICallback mCallback;

    interface APICallback {
        void setPMValuesAndDatesAPI(List<Object> pmValuesAndDatesAPI);
    }

    APIHelper(APICallback callback) {
        mCallback = callback;
    }

    private static List<Object> downloadPMValues(Integer pm25Sensor, Integer pm10Sensor) {
        String rawData;
        String pm25LatestStringDate = null; // i = 0
        String pm10LatestStringDate = null; // i = 1
        String pm25LatestStringValue = null; // i = 0
        String pm10LatestStringValue = null; // i = 1

        try {
            for (int i=0; i<2; i++) {
                HttpGetRequest getRequest = new HttpGetRequest();

                if (i == 0) {
                    rawData = getRequest.doHttpRequest(STATION_PM25_URL + '/' + pm25Sensor);
                } else {
                    rawData = getRequest.doHttpRequest(STATION_PM10_URL + '/' + pm10Sensor);
                }

                JSONObject jsonData = new JSONObject(rawData);  // return python's {key: value} of the provided link
                JSONArray itemsArray = jsonData.getJSONArray("values");  // return array of dicts from "values" value

                for (int j = itemsArray.length() - 1; j >= 0; j--) {  // to load last not-null value (last current value)
                    JSONObject specificDate = itemsArray.getJSONObject(j);

                    String date = specificDate.getString("date");
                    String value = specificDate.getString("value");

                    if (!value.equals("null")) {
                        if (i == 0) {
                            pm25LatestStringValue = value;
                            pm25LatestStringDate = date;
                        } else {
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
            return setEmptyList();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static Double[] convertToPercent(Double[] pmDoubles) {
        Double[] pmDoublesPerc = new Double[2];

        pmDoublesPerc[0] = 4 * pmDoubles[0];  // PM2.5
        pmDoublesPerc[1] = 2 * pmDoubles[1];  // PM10

        return pmDoublesPerc;
    }

    static class PMLoader extends AsyncTaskLoader<List<Object>> {
        PMLoader(Context context) {
            super(context);
        }

        @Override
        public List<Object> loadInBackground() {
            return downloadPMValues(3731, 3730);  // sensors from my closest station
        }
    }

    private static List<Object> setEmptyList() {
        List<Object> empty = new ArrayList<>(2);
        Double[] emptyDouble = new Double[]{0.0, 0.0};
        String[] emptyString = new String[]{"no data", "no data"};

        empty.add(emptyDouble);
        empty.add(emptyString);

        mCallback.setPMValuesAndDatesAPI(empty);
        return empty;
    }
}
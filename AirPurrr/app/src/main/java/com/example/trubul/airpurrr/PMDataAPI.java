package com.example.trubul.airpurrr;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by krzysiek
 * On 3/13/18.
 */

public class PMDataAPI {

    private static final String TAG = "PMDataAPI";
    private static final String PM25_DATA_API_URL = "http://api.gios.gov.pl/pjp-api/rest/data/getData/3731";
    private static final String PM10_DATA_API_URL = "http://api.gios.gov.pl/pjp-api/rest/data/getData/3730";
    private Context mContext;
    private MyCallback mCallback;

    public interface MyCallback {
        void setCurrentPMAPI(List<Object> currentPMAPI);
    }

    public PMDataAPI(Context context, MyCallback callback) {
        mContext = context;
        mCallback = callback;
    }

    public List<Object> downloadPMDataAPI() {
        String pmDataAPI;
        String pm25LatestStringDate = null; // i = 0
        String pm10LatestStringDate = null; // i = 1
        String pm25LatestStringValue = null; // i = 0
        String pm10LatestStringValue = null; // i = 1
        Double pm25LatestDoubleValue;
        Double pm10LatestDoubleValue;

        try {
            for (int i=0; i<2; i++) {
                HttpGetRequest getRequest = new HttpGetRequest();

                if (i == 0) {
                    pmDataAPI = getRequest.execute(PM25_DATA_API_URL).get();
                } else {
                    pmDataAPI = getRequest.execute(PM10_DATA_API_URL).get();
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

            pm25LatestDoubleValue = Double.parseDouble(pm25LatestStringValue);
            pm10LatestDoubleValue = Double.parseDouble(pm10LatestStringValue);

            Double[] pmDoubles = new Double[]{ pm25LatestDoubleValue, pm10LatestDoubleValue } ;
            String[] pmDates = new String[]{ pm25LatestStringDate, pm10LatestStringDate };

            List<Object> pmDoublesDates = new ArrayList<>(2);
            pmDoublesDates.add(pmDoubles);
            pmDoublesDates.add(pmDates);

            mCallback.setCurrentPMAPI(pmDoublesDates);
            return pmDoublesDates;

        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        catch (ExecutionException e) {
            e.printStackTrace();
        }
        catch (NullPointerException e) {
            Toast.makeText(mContext, "Błąd połączenia z serwerem" , Toast.LENGTH_LONG).show();
            List<Object> empty = new ArrayList<>(2);
            Double[] emptyDouble = new Double[]{0.0, 0.0};
            String[] emptyString = new String[]{"", ""};
            empty.add(emptyDouble);
            empty.add(emptyString);

            mCallback.setCurrentPMAPI(empty);
            return empty;
        }
        catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "onDownloadComplete: Error processing Json data " + e.getMessage());
        }

        return null;
    }

}

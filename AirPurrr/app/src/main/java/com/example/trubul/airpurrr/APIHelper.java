package com.example.trubul.airpurrr;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.location.Location;
import android.support.v4.content.Loader;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

class APIHelper {
    private static final String TAG = "APIHelper";
    private static final String STATION_LOCATIONS_URL = "http://api.gios.gov.pl/pjp-api/rest/station/findAll";
    private static final String STATION_SENSORS_URL = "http://api.gios.gov.pl/pjp-api/rest/station/sensors";
    private static final String STATION_PM25_URL = "http://api.gios.gov.pl/pjp-api/rest/data/getData";
    private static final String STATION_PM10_URL = "http://api.gios.gov.pl/pjp-api/rest/data/getData";

    private static APICallback mCallback;

    interface APICallback {
        void setPMValuesAndDatesAPI(List<Object> pmValuesAndDatesAPI);
        }

    APIHelper(APICallback callback) {
        mCallback = callback;
    }

    static List<List<Object>> downloadStationLocations() {
        String rawData;
        List<List<Object>> stationList = new ArrayList<>();

        try {
            HttpGetRequest getRequest = new HttpGetRequest();
            rawData = getRequest.doHttpRequest(STATION_LOCATIONS_URL);

            JSONArray jsonData = new JSONArray(rawData);
            for (int i = 0; i < jsonData.length(); i++) {
                JSONObject specificStation = jsonData.getJSONObject(i);

                Integer id = specificStation.getInt("id");
                String latitudeString = specificStation.getString("gegrLat");
                String longitudeString = specificStation.getString("gegrLon");
                Double latitude = Double.parseDouble(latitudeString);
                Double longitude = Double.parseDouble(longitudeString);

                List<Object> station = new ArrayList<>(3);
                station.add(0, id);
                Location stationLocation = convertToLocation(latitude, longitude);
                station.add(1, stationLocation);

                if (!station.isEmpty()) {
                    stationList.add(station);
                }
            }
            return stationList;

        } catch (NullPointerException e) {
            Log.d(TAG, "downloadStationLocation: Empty stations loc data " + e.getMessage());
            return new ArrayList<>();
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "downloadStationLocation: Error processing JSON data " + e.getMessage());
        }

        return null;
    }

    private static Location convertToLocation(Double latitude, Double longitude) {
        Location stationLocation = new Location("API");

        stationLocation.setLatitude(latitude);
        stationLocation.setLongitude(longitude);

        return stationLocation;
    }

    private static int findClosestStation(List<List<Object>> stations) {

        while (!LoginActivity.isLocation) { Log.d(TAG, "findClosestStation: still can't get a location"); }

        Location lastLocation = LoginActivity.mLocationService.getLastLocation();
        Log.d(TAG, "findClosestStation: lastLocation is: " + lastLocation);

        float[] results = {0};

        Float closestDistance = -1f;
        Integer closestDistanceId = 0;

        for(List<Object> station : stations) {
            Integer id = (Integer) station.get(0);
            Location stationLocation = (Location) station.get(1);

            // Calculate distance in meters
            Location.distanceBetween(lastLocation.getLatitude(), lastLocation.getLongitude(),
                    stationLocation.getLatitude(), stationLocation.getLongitude(), results);

            float distance = results[0];
            if (closestDistance == -1f || distance < closestDistance) {  // set first distance as closest distance
                closestDistance = distance;
                closestDistanceId = id;
            }
        }

        return closestDistanceId;
    }

    static Integer[] downloadStationSensors(Integer stationId) {
        String rawData;
        Integer[] sensorsArray = {0, 0};

        try {
            HttpGetRequest getRequest = new HttpGetRequest();
            rawData = getRequest.doHttpRequest(STATION_SENSORS_URL + '/' + stationId);

            JSONArray jsonData = new JSONArray(rawData);
            for (int i = 0; i < jsonData.length(); i++) {
                JSONObject specificSensor = jsonData.getJSONObject(i);
                JSONObject sensorDetails = specificSensor.getJSONObject("param");

                if (sensorDetails.getString("paramCode").equals("PM2.5")) {
                    sensorsArray[0] = specificSensor.getInt("id");
                }
                if (sensorDetails.getString("paramCode").equals("PM10")) {
                    sensorsArray[1] = specificSensor.getInt("id");
                }
            }
            return sensorsArray;

        } catch (NullPointerException e) {
            Log.d(TAG, "downloadStationLocation: Empty stations sensors data " + e.getMessage());
            return new Integer[]{0, 0};
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "downloadStationLocation: Error processing JSON data " + e.getMessage());
        }

        return null;
    }

    static List<Object> downloadPMValues(Integer pm25Sensor, Integer pm10Sensor) {
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
            Log.e(TAG, "downloadPMValues: Error processing JSON data " + e.getMessage());
        }

        return null;
    }

    private static Double[] convertToPercent(Double[] pmDoubles) {
        Double[] pmDoublesPerc = new Double[2];

        pmDoublesPerc[0] = 4 * pmDoubles[0];  // PM2.5
        pmDoublesPerc[1] = 2 * pmDoubles[1];  // PM10

        return pmDoublesPerc;
    }

    static class PMLoader extends android.support.v4.content.AsyncTaskLoader<List<Object>> {
        PMLoader(Context context) {
            super(context);
        }

        @Override
        public List<Object> loadInBackground() {
            List<List<Object>> stationList = APIHelper.downloadStationLocations();

            if (stationList != null) {
                Integer closestStation = findClosestStation(stationList);
                Log.d(TAG, "loadInBackground: closestStation is: " + closestStation);
                Integer[] stationSensors = downloadStationSensors(closestStation);

                if (stationSensors != null && stationSensors[0] != 0) {  // stationSensors[0] = 0 if Location is {0.0, 0.0}
                    Log.d(TAG, "loadInBackground: stationSensors are: " + stationSensors[0] + ", " + stationSensors[1]);
                    return downloadPMValues(stationSensors[0], stationSensors[1]);
                } else {
                    return downloadPMValues(3731, 3730);  // sensors from my closest station
                }
            } else {
                return setEmptyList();
            }
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
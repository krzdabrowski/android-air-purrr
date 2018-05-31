package com.example.trubul.airpurrr;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class LocationService extends Service {
    private static final String TAG = "LocationService";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 0;
    private static final float LOCATION_DISTANCE = 1000;
    private static Location mLastLocation;

//    private SharedPreferences mSharedPreferences;
//    private static final String LATITUDE_KEY = "LatitudeKey";
//    private static final String LONGITUDE_KEY = "LongitudeKey";


    public static Location getLastLocation() {
        return mLastLocation;
    }

    class LocationListener implements android.location.LocationListener {

        LocationListener(String provider) { mLastLocation = new Location(provider); }

        @Override
        public void onLocationChanged(Location location) {
            mLastLocation.set(location);
            Toast.makeText(LocationService.this, "LOCATION IS: " + String.valueOf(mLastLocation.getLatitude()) + "; " + String.valueOf(mLastLocation.getLongitude()), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onProviderDisabled(String provider) { }

        @Override
        public void onProviderEnabled(String provider) { }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) { }
    }

    LocationListener mLocationListener = new LocationListener(LocationManager.NETWORK_PROVIDER);


    @Override
    public IBinder onBind(Intent arg0) { return null; }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
//        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }

        try {
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE, mLocationListener);
        } catch (java.lang.SecurityException e) {
            Log.i(TAG, "Failed to request location update", e);
        } catch (IllegalArgumentException e) {
            Log.d(TAG, "network provider does not exist, " + e.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLocationManager != null) {
            try {
                mLocationManager.removeUpdates(mLocationListener);
            } catch (Exception e) {
                Log.i(TAG, "Failed to remove location listeners", e);
            }
        }
    }
}
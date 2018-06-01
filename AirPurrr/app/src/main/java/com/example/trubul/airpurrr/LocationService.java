package com.example.trubul.airpurrr;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class LocationService extends Service {
    private static final String TAG = "LocationService";
    private MyBinder binder;
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000 * 60;  // auto-update location every 1 min
    private static final float LOCATION_DISTANCE = 0;  // should be 0 as user doesn't move much
    private Location mLastLocation;

    public Location getLastLocation() {
        return mLastLocation;
    }

    class LocationListener implements android.location.LocationListener {

        LocationListener(String provider) { mLastLocation = new Location(provider); }

        @Override
        public void onLocationChanged(Location location) {
            mLastLocation.set(location);
            Toast.makeText(LocationService.this, "LOCATION IS: " + String.valueOf(mLastLocation.getLatitude()) + "; " + String.valueOf(mLastLocation.getLongitude()), Toast.LENGTH_LONG).show(); // TODO: to comment-out later
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
    public void onCreate() {
        binder = new MyBinder();

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

    public class MyBinder extends Binder {
        public LocationService getServiceSystem() {
            return LocationService.this;
        }
    }

    @Override
    public IBinder onBind(Intent arg0) { return binder; }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

}
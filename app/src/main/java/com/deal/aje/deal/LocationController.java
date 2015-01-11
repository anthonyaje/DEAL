package com.deal.aje.deal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;

/**
 * Created by Lalala on 1/12/15.
 */
public class LocationController {

    // Acquire a reference to the system Location Manager
    private LocationManager locationManager;
    private LocationListener locationListener;

    private double curLat;
    private double curLong;

    private String provider = LocationManager.NETWORK_PROVIDER;

    private LocationController() {
        locationListener= new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                curLat =  (location.getLatitude());
                curLong =  (location.getLongitude());
            }
            public void onStatusChanged(String provider, int status, Bundle extras) {}
            public void onProviderEnabled(String provider) {}
            public void onProviderDisabled(String provider) {}
        };
    }

    public static LocationController getInstance() {
        return WriterHolder.INSTANCE;
    }

    private static class WriterHolder {

        private static final LocationController INSTANCE = new LocationController();
    }

    public Location getLocation(Activity activity)
    {
        //Getting Location
        boolean locEnabled;
        locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        locEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!locEnabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            activity.startActivity(intent);
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        Location location = locationManager.getLastKnownLocation(provider);
        return location;
    }

    public double getCurLat() {
        return curLat;
    }

    public double getCurLong() {
        return curLong;
    }

    public String getProvider() {
        return provider;
    }
}

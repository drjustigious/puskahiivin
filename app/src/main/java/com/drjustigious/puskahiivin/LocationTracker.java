package com.drjustigious.puskahiivin;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

public class LocationTracker  extends Service implements LocationListener {

    private final Context mContext;
    private static final long MIN_DISTANCE_BETWEEN_LOCATIONS = 0; // in meters
    private static final long MIN_TIME_BETWEEN_LOCATIONS = 500; // in milliseconds


    private boolean gpsLocationEnabled = false;
    private boolean networkLocationEnabled = false;
    private boolean locationAvailable = false;

    public Location currentLocation;
    protected LocationManager locationManager;

    public LocationTracker(Context context) {
        this.mContext = context;
    }

    public void startLocation() {
        try {
            log("Starting location tracker");

            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
            gpsLocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            networkLocationEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if(gpsLocationEnabled || networkLocationEnabled) {
                this.locationAvailable = true;

                if (networkLocationEnabled) {
                    log("Network location enabled");

                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BETWEEN_LOCATIONS,
                            MIN_DISTANCE_BETWEEN_LOCATIONS, this);

                    if (locationManager != null) {
                        currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                        if (currentLocation != null) {
                            log("Network latitude:  "+currentLocation.getLatitude());
                            log("Network longitude: "+currentLocation.getLongitude());
                        }
                    }
                }

                if (gpsLocationEnabled) {
                    log("GPS location enabled");

                    locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                            MIN_TIME_BETWEEN_LOCATIONS,
                            MIN_DISTANCE_BETWEEN_LOCATIONS, this);

                    if (locationManager != null) {
                        currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                        if (currentLocation != null) {
                            log("GPS latitude:  "+currentLocation.getLatitude());
                            log("GPS longitude: "+currentLocation.getLongitude());
                        }
                    }
                }
            }

            log("Location tracker running");
        }
        catch (SecurityException e) {
            // There was most likely a problem with the location permissions
            e.printStackTrace();
        }
    }


    public void stopLocation(){
        if(locationManager != null){
            locationManager.removeUpdates(LocationTracker.this);
        }
    }

    public String getCurrentPositionCoordinates() {
        if (currentLocation == null) {
            return "???° N, ???° E";
        }

        double lat = currentLocation.getLatitude();
        double lon = currentLocation.getLongitude();

        return Double.toString(lat)+"° N, "+Double.toString(lon)+"° E";
    }

    @Override
    public void onLocationChanged(Location newLocation) {
        currentLocation = newLocation;
        log("New latitude:  "+newLocation.getLatitude());
        log("New longitude: "+newLocation.getLongitude());
    }

    @Override
    public void onProviderDisabled(String provider) {
        log("Provider "+provider+" disabled");
    }

    @Override
    public void onProviderEnabled(String provider) {
        log("Provider "+provider+" enabled");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        log("Provider "+provider+" status changed to "+status);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    private void log(String message) {
        System.out.println("[LocationTracker] "+message);
    }
}

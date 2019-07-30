package com.drjustigious.puskahiivin;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.GnssStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

public class LocationTracker  extends Service implements LocationListener {

    private final Context mContext;
    private static final long MIN_DISTANCE_BETWEEN_LOCATIONS = 5; // in meters
    private static final long LOCATION_INTERVAL_REALTIME = 500; // milliseconds
    private static final long LOCATION_INTERVAL_BACKGROUND = 20*1000; // milliseconds

    public enum LocationInterval {
        Realtime,
        Background
    }


    private boolean gpsLocationEnabled = false;
    private boolean networkLocationEnabled = false;
    private boolean locationAvailable = false;
    private String locationProvider = null;

    private static long locationInterval = LOCATION_INTERVAL_REALTIME;

    public Location currentLocation;
    protected LocationManager locationManager;

    private GnssStatus.Callback satelliteStatusCallback;
    private GnssStatus satelliteStatus;

    public LocationTracker(Context context) {
        this.mContext = context;
    }

    public void restartLocation() {
        try {
            log("Restarting location tracker");

            // Clear any existing location requests
            if(locationManager != null){
                locationManager.removeUpdates(LocationTracker.this);
            }

            // Connect to the location service
            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
            if (locationManager == null) {
                log("WARNING: Location service unavailable");
                locationProvider = null;
                return;
            }

            // Subscribe to satellite status updates
            satelliteStatusCallback = new GnssStatus.Callback() {
                @Override
                public void onSatelliteStatusChanged(GnssStatus status) {
                    updateSatelliteStatus(status);
                }
            };
            locationManager.registerGnssStatusCallback(satelliteStatusCallback);

            // Attempt to get a location provider
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationProvider = LocationManager.GPS_PROVIDER;
                log("Using GNSS location");
            }
            else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationProvider = LocationManager.NETWORK_PROVIDER;
                log("No GNSS location available, falling back to network location");
            }
            else {
                locationProvider = null;
                log("WARNING: No location provider available");
                return;
            }

            // Refresh the location
            locationManager.requestLocationUpdates(
                    locationProvider,
                    locationInterval,
                    MIN_DISTANCE_BETWEEN_LOCATIONS,
                    this);

            currentLocation = locationManager.getLastKnownLocation(locationProvider);
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
    public void onProviderDisabled(String disabledProvider) {
        log("Provider "+disabledProvider+" disabled");
        if (disabledProvider.equals(locationProvider)) {
            restartLocation();
        }
    }

    @Override
    public void onProviderEnabled(String newProvider) {
        log("Provider "+newProvider+" enabled");
        if (locationProvider == null || (locationProvider.equals(LocationManager.NETWORK_PROVIDER) && newProvider.equals(LocationManager.GPS_PROVIDER))) {
            restartLocation();
        }
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

    private void updateSatelliteStatus(GnssStatus status) {
        satelliteStatus = status;
    }
}

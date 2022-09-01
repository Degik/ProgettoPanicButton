package com.example.progettopanicbutton;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

@SuppressLint("MissingPermission")
public class ServiceTracker extends Service {
    // Context
    private Context context = this;
    // Status service (static)
    public static boolean statusServiceTracker = false;
    // Parametri
    private long minTimeUpdate = 3000;
    private long minDistanceUpdate = 10;
    // LocationMenager e Listener
    private LocationManager locationManager;
    private LocationListener listener;
    // Location (static)
    private static Location myLocation;
    private static double latitude;
    private static double longitude;
    // Intent
    public static Intent serviceTrackerIntent;


    public ServiceTracker() {

    }

    @Override
    public void onCreate(){
        statusServiceTracker = true;
        if(locationManager == null){
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }
        if (locationManager != null) {
            try {
                listener = new LocationListener() {
                    @Override
                    public void onLocationChanged(@NonNull Location location) {
                        myLocation = location;
                    }
                };
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minDistanceUpdate, minTimeUpdate, listener);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("ServiceTracker avviato con successo!");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        serviceTrackerIntent = intent;
    }

    @Override
    public void onDestroy(){
        locationManager.removeUpdates(listener);
        statusServiceTracker = false;
        System.out.println("ServizioTracker fermato!");
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public static Location getMyLocation(){
        // TODO: Testare il location
        return myLocation;
    }

    // Aggiorno le cordinate
    // Da usare prima della notifica o anche in altre situazioni
    public static double getLatitude() {
        latitude = myLocation.getLatitude();
        return latitude;
    }

    public static double getLongitude(){
        longitude = myLocation.getLongitude();
        return longitude;
    }



}
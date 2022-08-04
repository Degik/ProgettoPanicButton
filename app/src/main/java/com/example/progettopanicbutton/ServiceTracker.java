package com.example.progettopanicbutton;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import java.util.LinkedList;
import java.util.List;

@SuppressLint("MissingPermission")
public class ServiceTracker extends Service {
    // Parametri
    private long minTimeUpdate = 3000;
    private long minDistanceUpdate = 10;
    // LocationMenager e Listener
    private LocationManager locationManager;
    private LocationListener listener;
    // Location
    private static Location myLocation;


    public ServiceTracker() {

    }

    @Override
    public void onCreate(){
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
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public Location getMyLocation(){
        return myLocation;
    }
}
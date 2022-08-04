package com.example.progettopanicbutton;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;

import androidx.core.app.ActivityCompat;

public class ServiceTracker extends Service {
    private long minTimeUpdate = 3000;
    private float minDistanceUpdate = 10;
    private LocationManager locationManager;
    private LocationListener listener;


    public ServiceTracker() {

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate(){
        if(locationManager == null){
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }
        if (locationManager != null){
            try{
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, (long) minDistanceUpdate, minTimeUpdate, listener);
            } catch(Exception e){
                e.printStackTrace();
            }
            try {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, (long) minDistanceUpdate, minTimeUpdate, listener);
            } catch (Exception e){
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
}
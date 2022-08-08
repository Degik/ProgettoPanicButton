package com.example.progettopanicbutton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    // Permission
    private int PERMISSION_ID = 44;
    // RequestCode
    // BottomoNavigationView
    private BottomNavigationView bottomNavigationView;
    // Settings
    private boolean gpsTrack;
    private boolean voiceRecord;
    private boolean cameraPhoto;
    //Fragment
    private Contacts contactsFragment = new Contacts();
    private Panic panicFragment = new Panic();
    // Location info
    private Location location;
    private double latitude;
    private double longitude;
    // ServiceIntentTracker
    private Intent intentServiceTracker;
    // Button
    private Button floatingActionButtonAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation_view);

        // Imposto la schermata iniziale sul fragment del panico
        bottomNavigationView.setSelectedItemId(R.id.navigation_panic);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, panicFragment).commit();
        bottomNavigationView.setOnNavigationItemSelectedListener(this);


        /*
            Raccolgo le impostazioni
        */
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        gpsTrack = sharedPreferences.getBoolean("gpsTrack", false);

        // Se il gps è impostato faccio partire il service
        if(gpsTrack){
            // Controllo i permessi
            if(checkLocationPermission()){
                // startServiceTracker
                startServiceTracker();

            } else {
                // Richiedo i permessi
                requestLocationPermission();
            }
        } else {
            //  TODO: Lavorare al termine del servizio di tracker
            if(ServiceTracker.statusServiceTracker){
                stopService(intentServiceTracker);
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.navigation_panic:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, panicFragment).commit();
                return true;

            case R.id.navigation_contacts:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, contactsFragment).commit();
                return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.menu_settings:
                newSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            // Prendiamo un contatto
            case Contacts.REQUEST_PICK:
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    Cursor cursor = getContentResolver().query(contactData, null, null, null, null);
                    if (cursor.moveToFirst()) {
                        String id = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                        //
                        contactsFragment.addContact(id);
                        System.out.println(id);
                        //
                    }
                    cursor.close();
                }
                break;
        }
    }

    /////////////////////////
    // METODI PER I PERMESSI
    /////////////////////////
    private boolean checkLocationPermission(){
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission(){
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    /////////////////////////
    // METODI PER GLI INTENT
    /////////////////////////
    // Apro l'activity per il settings
    private void newSettings(){
        Intent intentSettings = new Intent(this, SettingsActivity.class);
        startActivity(intentSettings);
    }

    // Avvio il service per la geolocalizzazione
    private void startServiceTracker(){
        intentServiceTracker = new Intent(this, ServiceTracker.class);
        startService(intentServiceTracker);
    }
}
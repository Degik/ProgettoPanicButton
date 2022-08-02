package com.example.progettopanicbutton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView bottomNavigationView;
    //
    //Fragment
    private Contacts contactsFragment = new Contacts();
    private Panic panicFragment = new Panic();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation_view);

        bottomNavigationView.setSelectedItemId(R.id.navigation_panic);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);


        /*
            Raccolgo un valore dalle sharedPref per prova
        */
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String signature = sharedPreferences.getString("signature", "");
        System.out.println(signature);
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

    private void newSettings(){
        Intent intentSettings = new Intent(this, SettingsActivity.class);
        startActivity(intentSettings);
    }
}
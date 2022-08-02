package com.example.progettopanicbutton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView bottomNavigationView;
    //
    //Fragment
    Contacts contactsFragment = new Contacts();
    Panic panicFragment = new Panic();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation_view);

        bottomNavigationView.setSelectedItemId(R.id.navigation_panic);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
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
}
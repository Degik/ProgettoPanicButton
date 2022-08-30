package com.example.progettopanicbutton;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import java.util.Set;

public class Backup {
    private final String FAVOURITE = "favouriteID";
    private final String FIRSTSTART = "firststart";
    // Context
    private Context context;
    // SharedPreferences
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public Backup(Context context){
        this.context = context;
    }

    /**
     * Questo metodo va chiamato prima di gpsEnabled, recordingEnabled, callEnabled e getSignature
     */
    public void setupDefault(){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean gpsEnabled(){
        return sharedPreferences.getBoolean("gpsTrack", false);
    }

    public boolean recordingEnabled(){
        return sharedPreferences.getBoolean("voiceRecord", false);
    }

    public boolean callEnabled(){
        return sharedPreferences.getBoolean("callPhone", false);
    }

    public String getSignature(){
        return sharedPreferences.getString("signature", "DEFAULT");
    }

    public void setFirstStart(){
        SharedPreferences firstStart = context.getSharedPreferences(FIRSTSTART, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = firstStart.edit();
        editor.putBoolean("firstStart", false);
        editor.commit();
    }

    public boolean getFirstStart(){
        SharedPreferences settings = context.getSharedPreferences(FIRSTSTART, context.MODE_PRIVATE);
        return settings.getBoolean("firstStart", true);
    }

    /**
     * Effuttua il backup della lista
     */
    public void makeBackup(){
        editor = sharedPreferences.edit();
        editor.putStringSet(FAVOURITE, MainActivity.favourite_ID);
        editor.apply();
    }

    public Set<String> getBackup(){
        Set<String> list = sharedPreferences.getStringSet(FAVOURITE, null);
        return list;
    }
}

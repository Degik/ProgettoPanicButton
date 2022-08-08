package com.example.progettopanicbutton;

import android.net.Uri;

public class InfoContact {
    private String id;
    private String name;
    private String number;
    private String email;
    private Uri photo;

    public InfoContact(String id, String name, String number, String email, Uri photo){
        this.id = id;
        this.name = name;
        this.number = number;
        this.email = email;
        this.photo = photo;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public String getEmail() {
        return email;
    }

    public Uri getPhoto() {
        return photo;
    }
}

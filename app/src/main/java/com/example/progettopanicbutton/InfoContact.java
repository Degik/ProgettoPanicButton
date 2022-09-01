package com.example.progettopanicbutton;

import android.net.Uri;

import java.util.Objects;

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

    public InfoContact(String name, String number, Uri photo){
        this.name = name;
        this.number = number;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InfoContact that = (InfoContact) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(number, that.number) && Objects.equals(email, that.email) && Objects.equals(photo, that.photo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

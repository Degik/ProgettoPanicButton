package com.example.progettopanicbutton;

import android.Manifest;
import android.app.Activity;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Contacts#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Contacts extends Fragment {

    // Struttura dati per salvare gli id dei contatti preferiti
    // TODO: Implementare il backup dei preferiti
    private HashSet<String> favourite_ID;
    // Permission
    private final int PERMISSION_ID = 44;
    // Button
    private FloatingActionButton floatingActionButtonAdd;
    // CursorAdapter
    private SimpleCursorAdapter cursorAdapter;
    // RequestCode (public static)
    public final static int REQUEST_PICK = 1;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Contacts() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Contacts.
     */
    // TODO: Rename and change types and number of parameters
    public static Contacts newInstance(String param1, String param2) {
        Contacts fragment = new Contacts();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        //
        favourite_ID = new HashSet<>();
        favourite_ID.add("1");
        favourite_ID.add("2");
        getContactsInformation();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contacts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Pulsante per aggiungere contatti
        floatingActionButtonAdd = (FloatingActionButton) view.findViewById(R.id.floatingActionButtonAdd);
        // Controllo se posso accedere ai contatti
        if (checkContactsPermission()) {
            //
            floatingActionButtonAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pickContact();
                }
            });
        } else {
            requestContactsPermission();
        }
    }

    /////////////////////////
    // METODI PER I PERMESSI
    /////////////////////////
    private boolean checkContactsPermission() {
        return ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestContactsPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{
                Manifest.permission.READ_CONTACTS}, PERMISSION_ID);
    }

    /////////////////////////
    // METODI PER GLI INTENT
    /////////////////////////
    // Il metodo chiama l'intent per segliere quale contatto prendere dalla lista di quelli già presenti sul telefono
    private void pickContact() {
        Intent intentPickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        try {
            getActivity().startActivityForResult(intentPickContact, REQUEST_PICK);

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void getContactsInformation(){
        String[] projection = {
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.HAS_PHONE_NUMBER,
                ContactsContract.Contacts.PHOTO_URI
        };
        String selection_ID = favourite_ID.toString().replace("[", "(").replace("]", ")");
        selection_ID = ContactsContract.Contacts._ID + " IN " + selection_ID;
        System.out.println(selection_ID);
        System.out.println(ContactsContract.Contacts._ID);
        Cursor cursor = getContext().getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, projection,  ContactsContract.Contacts._ID + " = 1", null, null);
        try{
            while(cursor.moveToNext()){
                String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                System.out.println(name);
            }
        } finally {
            cursor.close();
        }
    }

    public void addContact(String contact_ID){
        favourite_ID.add(contact_ID);
    }
}
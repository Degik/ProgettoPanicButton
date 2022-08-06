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

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Contacts#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Contacts extends Fragment {

    // Struttura dati per salvare gli id dei contatti preferiti
    // TODO: Implementare il backup dei preferiti
    private ArrayList<String> favouriteId;
    // Permission
    private final int PERMISSION_ID = 44;
    // Button
    private Button floatingActionButtonAdd;
    // CursorAdapter
    private SimpleCursorAdapter cursorAdapter;
    // RequestCode
    private final int REQUEST_PICK = 1;
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
        favouriteId = new ArrayList<>();
        // Controllo se posso accedere ai contatti
        if(checkContactsPermission()){
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Pulsante per aggiungere contatti
        floatingActionButtonAdd = (Button) getActivity().findViewById(R.id.floatingActionButtonAdd);
        return inflater.inflate(R.layout.fragment_contacts, container, false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case REQUEST_PICK:
                if(resultCode == Activity.RESULT_OK){
                    Uri contactData = data.getData();
                    Cursor cursor = getActivity().managedQuery(contactData, null, null, null, null);
                    if(cursor.moveToFirst()){
                        String id = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                        favouriteId.add(id);
                    }
                }
                break;
        }
    }

    /////////////////////////
    // METODI PER I PERMESSI
    /////////////////////////
    private boolean checkContactsPermission(){
        return ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestContactsPermission(){
        ActivityCompat.requestPermissions(getActivity(), new String[]{
                Manifest.permission.READ_CONTACTS}, PERMISSION_ID);
    }

    /////////////////////////
    // METODI PER GLI INTENT
    /////////////////////////
    // Il metodo chiama l'intent per segliere quale contatto prendere dalla lista di quelli già presenti sul telefono
    private void pickContact(){
        Intent intentPickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intentPickContact, REQUEST_PICK);
    }
}
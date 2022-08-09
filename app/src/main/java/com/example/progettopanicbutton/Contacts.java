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
    // In questa struttura vengono raccolte le informazioni di ogni contatto per la listView
    private ArrayList<InfoContact> contactArrayList;
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
        contactArrayList = new ArrayList<>();
        favourite_ID.add("3");
        favourite_ID.add("4");
        favourite_ID.add("5");
        getContactsInformation();


        //searchPhone();
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

    ///////////////////////////////////
    // METODI PER PRELEVARE INFOCONTACT
    ///////////////////////////////////
    private void getContactsInformation(){
        String[] projection = {
                ContactsContract.Contacts.NAME_RAW_CONTACT_ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.PHOTO_URI
        };
        String selection_ID = favourite_ID.toString().replace("[", "(").replace("]", ")");
        selection_ID = ContactsContract.Contacts._ID + " IN " + selection_ID;
        System.out.println(selection_ID);
        System.out.println(ContactsContract.Contacts._ID);
        Cursor cursor = getContext().getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, projection,  "_id = 5", null, null);
        try{
            while(cursor.moveToNext()){
                // Ricavo il nome
                String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                // Ricavo il contatto, per ricavarne il numero in seguito
                String contactID = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.NAME_RAW_CONTACT_ID));
                //
                String phone = takeNumber(contactID);
                String email = takeEmail(contactID);
                // Prendo l'immagine
                Uri photo = null;
                if(cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.PHOTO_URI)) != null){
                    photo = Uri.parse(cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.PHOTO_URI)));
                }
                // Creo infoContact
                InfoContact infoContact = new InfoContact(contactID, name, phone, email, photo);
                contactArrayList.add(infoContact);
            }
        } finally {
            cursor.close();
        }
    }

    /**
     * Questo metodo usa il cursor per trovare e prelevare il numero
     * dell'utente e ritornare {@code phoneNumber}
     * @param contact_ID Id dell'utente
     * @return phoneNubmer, altrimenti torna una stringa vuota
     */
    private String takeNumber(String contact_ID){
        String phoneNumber = "";
        // Costruisco la where per _id
        String selectionContact_ID = ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID + " = " + contact_ID;
        // Costruisco la select per NUMBER
        String[] projectionPhone = {
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };
        // Imposto il phoneCursor
        Cursor phoneCursor = getContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projectionPhone, selectionContact_ID,null, null);
        if(phoneCursor.moveToFirst()){
            phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
        }
        phoneCursor.close();
        return phoneNumber;
    }

    /**
     * Questo metodo usa il cursor per trovare e prelevare l'email
     * dell'utente e ritornare {@code email}
     * @param contact_ID Id dell'utente
     * @return email, altrimenti torna una strina vuota
     */
    private String takeEmail(String contact_ID){
        String email = "";
        // Costruisco la where per _id
        String selectionContact_ID = ContactsContract.CommonDataKinds.Email.RAW_CONTACT_ID + " = " + contact_ID;
        // Costruisco la select per EMAIL
        String[] projectionEmail = {
                ContactsContract.CommonDataKinds.Email.DATA
        };
        // Imposto emailCursor
        Cursor emailCursor = getContext().getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, projectionEmail, selectionContact_ID,null, null);
        if(emailCursor.moveToFirst()){
            if(ContactsContract.CommonDataKinds.Email.DATA != null){
                email = emailCursor.getString(emailCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Email.DATA));
            }
        }
        emailCursor.close();
        return email;
    }

    public void addContact(String contact_ID){
        favourite_ID.add(contact_ID);
    }
}
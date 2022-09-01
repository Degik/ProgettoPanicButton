package com.example.progettopanicbutton;

import android.Manifest;
import android.app.Activity;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.Context;
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
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Contacts#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Contacts extends Fragment {
    // Permission
    private final int PERMISSION_ID = 44;
    // Button
    private FloatingActionButton floatingActionButtonAdd;
    // ContactsAdapter
    private ContactsRecyclerAdapter adapter;
    // RecyclerView
    private RecyclerView recyclerView;
    // CursorAdapter
    private SimpleCursorAdapter cursorAdapter;
    // RequestCode (public static)
    private final int REQUEST_PICK = 1;
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

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
        getContactsInformation(getContext());
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
        setRecyclerView(view);
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
    public void getContactsInformation(Context context){
        String[] projection = {
                ContactsContract.Contacts.NAME_RAW_CONTACT_ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.PHOTO_URI
        };
        String selection_ID = MainActivity.favourite_ID.toString().replace("[", "(").replace("]", ")");
        selection_ID = ContactsContract.Contacts._ID + " IN " + selection_ID;
        System.out.println(selection_ID);
        System.out.println(ContactsContract.Contacts._ID);
        Cursor cursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, projection,  selection_ID, null, null);
        try{
            while(cursor.moveToNext()){
                // Ricavo il nome
                String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                // Ricavo il contatto, per ricavarne il numero in seguito
                String contactID = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.NAME_RAW_CONTACT_ID));
                //
                String phone = takeNumber(contactID, context);
                String email = takeEmail(contactID, context);
                // Prendo l'immagine
                Uri photo = null;
                if(cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.PHOTO_URI)) != null){
                    photo = Uri.parse(cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.PHOTO_URI)));
                }
                // Creo infoContact
                InfoContact infoContact = new InfoContact(contactID, name, phone, email, photo);
                // Per evitare duplicati controllo la loro presenza
                if(!MainActivity.contactInfoArrayList.contains(infoContact)){
                    MainActivity.contactInfoArrayList.add(infoContact);
                }
            }
        } finally {
            cursor.close();
        }
    }

    /**
     * Questo metodo usa il cursor per trovare e prelevare il numero
     * dell'utente e ritornare {@code phoneNumber}
     * @param contact_ID Id dell'utente
     * @param context
     * @return phoneNubmer, altrimenti torna una stringa vuota
     */
    private String takeNumber(String contact_ID, Context context){
        String phoneNumber = "";
        // Costruisco la where per _id
        String selectionContact_ID = ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID + " = " + contact_ID;
        // Costruisco la select per NUMBER
        String[] projectionPhone = {
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };
        // Imposto il phoneCursor
        Cursor phoneCursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projectionPhone, selectionContact_ID,null, null);
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
     * @param context
     * @return email, altrimenti torna una strina vuota
     */
    private String takeEmail(String contact_ID, Context context){
        String email = "";
        // Costruisco la where per _id
        String selectionContact_ID = ContactsContract.CommonDataKinds.Email.RAW_CONTACT_ID + " = " + contact_ID;
        // Costruisco la select per EMAIL
        String[] projectionEmail = {
                ContactsContract.CommonDataKinds.Email.DATA
        };
        // Imposto emailCursor
        Cursor emailCursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, projectionEmail, selectionContact_ID,null, null);
        if(emailCursor.moveToFirst()){
            if(ContactsContract.CommonDataKinds.Email.DATA != null){
                email = emailCursor.getString(emailCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Email.DATA));
            }
        }
        emailCursor.close();
        return email;
    }

    public void getContactInformation(Context context, String id){
        String[] projection = {
                ContactsContract.Contacts.NAME_RAW_CONTACT_ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.PHOTO_URI
        };
        Cursor cursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, projection,  ContactsContract.Contacts._ID + "=" + id, null, null);
        try{
            while(cursor.moveToNext()){
                // Ricavo il nome
                String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                // Ricavo il contatto, per ricavarne il numero in seguito
                String contactID = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.NAME_RAW_CONTACT_ID));
                //
                String phone = takeNumber(contactID, context);
                String email = takeEmail(contactID, context);
                // Prendo l'immagine
                Uri photo = null;
                if(cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.PHOTO_URI)) != null){
                    photo = Uri.parse(cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.PHOTO_URI)));
                }
                // Creo infoContact
                InfoContact infoContact = new InfoContact(contactID, name, phone, email, photo);
                // Per evitare duplicati controllo la loro presenza
                if(!MainActivity.contactInfoArrayList.contains(infoContact)){
                    MainActivity.contactInfoArrayList.add(0, infoContact);
                }
            }
        } finally {
            cursor.close();
        }
    }

    /**
     * Questo metodo imposta l'adapter per il recyclerView e lo swipe
     * @param view
     */
    private void setRecyclerView(@NonNull View view){
        recyclerView = (RecyclerView) getView().findViewById(R.id.contactsListView);
        adapter = new ContactsRecyclerAdapter(MainActivity.contactInfoArrayList, getContext());
        recyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        // Aggiungo lo swipe
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // Prendo la posizione dell'elemento
                int position = viewHolder.getAdapterPosition();
                // Prendo l'elemento
                InfoContact infoContactDeleted = MainActivity.contactInfoArrayList.get(viewHolder.getAdapterPosition());
                // Cancello l'elemento dalla lista
                MainActivity.contactInfoArrayList.remove(viewHolder.getAdapterPosition());
                // Elimino dalla favouriteID
                String idDeleted = infoContactDeleted.getId();
                MainActivity.favourite_ID.remove(idDeleted);
                // Aggiorno la lista
                adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                // Effettuo il backup
                MainActivity.backup.makeBackup();
                Snackbar.make(recyclerView, "Contatto eliminato", Snackbar.LENGTH_LONG).setAction("annulla", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Inserisco il vecchio elemento per "Undo"
                        MainActivity.contactInfoArrayList.add(position, infoContactDeleted);
                        // Inserisco in favouriteID
                        MainActivity.favourite_ID.add(idDeleted);
                        // Effettuo il backup
                        MainActivity.backup.makeBackup();
                        // Aggiorno
                        adapter.notifyItemInserted(position);
                    }
                }).show();
            }
        }).attachToRecyclerView(recyclerView);
    }

    /**
     * Notifico il cambiamento della lista
     */
    public void updateAdapter(){
        adapter.notifyItemInserted(0);
    }
}
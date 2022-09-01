package com.example.progettopanicbutton;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.StrictMode;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.telephony.TelephonyScanManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

import javax.mail.MessagingException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Panic#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Panic extends Fragment {
    // Button panic
    private Button panicButton;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Panic() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Panic.
     */
    // TODO: Rename and change types and number of parameters
    public static Panic newInstance(String param1, String param2) {
        Panic fragment = new Panic();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_panic, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        panicButton = (Button) view.findViewById(R.id.panicButton);
        panicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PanicManager panicManager = new PanicManager(getContext(), getActivity());
                MailSender mailSender = new MailSender();
                mailSender.sendMail(MainActivity.signature);
                if(MainActivity.gpsTrack){
                    // Gps
                    // Impostare la posizione nel messaggio
                    try {
                        mailSender.addLocation(getLocationAddress());
                    } catch (MessagingException e) {
                        Toast.makeText(getContext(), "Posizione non trovata", Toast.LENGTH_LONG).show();
                    }
                }
                if(MainActivity.voiceRecord){
                    // Record
                    // Impostare la registrazione nel messaggio
                    panicManager.startRecording();
                    File recordFile = panicManager.stopRecording(5);
                    System.out.println(recordFile.getAbsolutePath());
                    try {
                        mailSender.addAttachment(recordFile);
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                }
                //
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                
                if(MainActivity.voiceRecord || MainActivity.gpsTrack){
                    ThreadMail threadMail = new ThreadMail(mailSender);
                    Thread thread = new Thread(threadMail);
                    thread.start();
                }
                // Mandare i messaggi
                if(MainActivity.callPhone){
                    // Call
                    panicManager.startCall();
                }
            }
        });
    }

    private String getLocationAddress(){
        List<Address> addresses;
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        String address = null;
        try {
            addresses = geocoder.getFromLocation(ServiceTracker.getLatitude(), ServiceTracker.getLongitude(), 1);
            address = addresses.get(0).getAddressLine(0);
        } catch (IOException e) {
            //
        }
        return address;
    }

}
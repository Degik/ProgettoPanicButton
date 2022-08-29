package com.example.progettopanicbutton;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PanicManager {
    // Codice per le chiamate
    private final int REQUEST_CALL = 2;
    // Context
    private Context context;
    // Activity
    private Activity activity;
    // Recording
    private String currentRecordPath;
    private MediaRecorder recorder;
    private File fileRecord = null;
    // ContactFragment
    private Contacts contacts;

    // Invio email
    // Registrazione
    // Invio chiamata

    public PanicManager(Context context, Activity activity){
        this.context = context;
        this.activity = activity;
        if(MainActivity.contactInfoArrayList.isEmpty()){
            // Devo prelevare le informazioni dei contatti per la comunicazione di emergenza
            contacts = new Contacts();
            contacts.getContactsInformation(context);
        }
    }

    private File createRecordFile() throws IOException {
        String time = new SimpleDateFormat("dd:MM:yyyy_HH:mm:ss").format(new Date());
        String recordFileName = "REC_3GP_" + time + "_";
        File storageDir = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            storageDir = context.getExternalFilesDir(Environment.DIRECTORY_RECORDINGS);
        }
        File record = File.createTempFile(recordFileName,".3gp", storageDir);
        currentRecordPath = record.getAbsolutePath();
        return record;
    }

    /**
     * Il metodo crea un file dove verrà salvata la registrazione ed impostata dopo essere stata fermata
     * @param fileName
     */
    public void startRecording(String fileName){
        // Creo il file record
        try {
            fileRecord = createRecordFile();
        } catch(IOException e){
            e.printStackTrace();
        }
        // Imposto i parametri per la registrazione
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFile(fileName);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            recorder.prepare();
        } catch(Exception e){
            e.printStackTrace();
        }
        // Inizio la registrazione
        recorder.start();
    }

    /**
     * Il motodo termina la registrazione dopo il time
     * @param time
     */
    public void stopRecording(int time){
        sleep(time);
        recorder.stop();
        recorder.release();
    }

    public void startCall(String numberPhone){
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setData(Uri.parse("tel:" + numberPhone));
        activity.startActivityForResult(intent, REQUEST_CALL);
    }

    public void sendMail(){

    }

    private static void sleep(int time){
        try {
            for(int i = 0; i < time; i++){
                Thread.sleep(1000);
            }
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}

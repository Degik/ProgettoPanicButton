package com.example.progettopanicbutton;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.icu.text.IDNA;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PanicManager {
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
    // InfoContact
    private InfoContact infoContact;

    //first boolean
    private boolean first = true;

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
     */
    public void startRecording(){
        // Creo il file record
        try {
            fileRecord = createRecordFile();
        } catch(IOException e){
            e.printStackTrace();
        }
        // Imposto i parametri per la registrazione
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFile(currentRecordPath);
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
    public Uri stopRecording(int time){
        sleep(time);
        recorder.stop();
        recorder.release();
        return FileProvider.getUriForFile(context, "com.example.progettopanicbutton.fileprovider", fileRecord);
    }

    public void startCall(){
        infoContact = MainActivity.contactInfoArrayList.get(0);
        call(infoContact.getNumber());
        // Listener
        PhoneCallListener phoneCallListener = new PhoneCallListener();
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(phoneCallListener, PhoneCallListener.LISTEN_CALL_STATE);
    }

    private void call(String numberPhone){
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + numberPhone));
        activity.startActivity(intent);
        // Trovo l'indice
        int indexContact = MainActivity.contactInfoArrayList.indexOf(infoContact);
        // Porto avanti l'indice e aggiorno infoContact
        if(indexContact != MainActivity.contactInfoArrayList.size() - 1){
            indexContact++;
            infoContact = MainActivity.contactInfoArrayList.get(indexContact);
        }
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

    private class PhoneCallListener extends PhoneStateListener {
        private boolean isPhoneCalling = false;

        @Override
        public void onCallStateChanged(int state, String phoneNumber) {
            super.onCallStateChanged(state, phoneNumber);
            // TODO: Bugfix
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    //CALL_STATE_IDLE;
                    if(isPhoneCalling){
                        Intent i = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        call(infoContact.getNumber());
                        isPhoneCalling = false;
                    }
                    break;

                case TelephonyManager.CALL_STATE_OFFHOOK:
                    //CALL_STATE_OFFHOOK;
                    //ACTIVE
                    isPhoneCalling = true;
                    break;

                case TelephonyManager.CALL_STATE_RINGING:
                    //CALL_STATE_RINGING
                    break;

                default:
                    break;
            }
        }
    }
}

package com.example.progettopanicbutton;

import android.content.Context;
import android.widget.Toast;

import javax.mail.MessagingException;

public class ThreadMail implements Runnable{
    private MailSender mailSender;

    public ThreadMail(MailSender mailSender){
        this.mailSender = mailSender;
    }

    @Override
    public void run() {
        for(InfoContact infoContact: MainActivity.contactInfoArrayList){
            try {
                if(!infoContact.getEmail().equals("")){
                    mailSender.addRecipient(infoContact.getEmail());
                    mailSender.sendMail();
                }
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
    }
}

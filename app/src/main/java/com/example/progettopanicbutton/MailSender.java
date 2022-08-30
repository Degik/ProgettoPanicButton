package com.example.progettopanicbutton;

import android.net.Uri;

import java.util.ArrayList;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;

public class MailSender {
    private final String username = "mailtestdvb@gmail.com";
    private final String password = "isa990021";
    //
    private ArrayList<String> contacts;
    //
    private Properties properties;
    private Session session;
    private MimeMessage message;

    public MailSender(ArrayList<String> contacts){
        this.contacts = contacts;
    }

    public void sendMail(String subject, String messageTxt){
        configProperties();
        setSession();
        try{
            message = new MimeMessage(session);
            setupMessage(subject, messageTxt);
        } catch (MessagingException e){
            e.printStackTrace();
        }
    }

    private void configProperties(){
        properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.socketFactory.port", "465");
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
    }

    private void setSession(){
        session = Session.getInstance(properties,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
    }

    private void setupMessage(String subject, String messageTxt) throws MessagingException {
        message.setFrom(new InternetAddress(username));
        message.setSubject(subject);
        message.setText(messageTxt);
        Transport.send(message);
    }

    private void addAttachment(Multipart multipart, String filename){
        //DataSource
    }
}

package com.example.progettopanicbutton;

import android.net.Uri;

import java.io.File;
import java.util.ArrayList;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.activation.DataSource;
import javax.mail.internet.MimeMultipart;

public class MailSender {
    private final String username = "bulotta@outlook.com";
    private final String password = "davide990021";
    //
    private Properties properties;
    private Session session;
    private MimeMessage message;
    private Multipart multipart;

    public MailSender(){
        //
    }

    public void sendMail(String subject){
        configProperties();
        setSession();
        try{
            message = new MimeMessage(session);
            setupMessage(subject);
        } catch (MessagingException e){
            e.printStackTrace();
        }
    }
    /*
    private void configProperties(){
        properties = new Properties();
        properties.setProperty("mail.smtp.host", "smtp");
        properties.setProperty("mail.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.socketFactory.port", "465");
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.socketFactory.fallback", "false");
        properties.setProperty("mail.smtp.quitwait", "false");

        session = Session.getInstance(properties, this);
    }*/

    private  void configProperties(){
        properties = new Properties();
        properties.put("mail.smtp.user", username);
        properties.put("mail.smtp.host", "smtp-mail.outlook.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.starttls.enable","true");
        properties.put("mail.smtp.auth", "true");

        //session = Session.getInstance(properties, this);
    }

    /*
    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(username,password);
    }*/

    private void setSession(){
        session = Session.getInstance(properties,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
    }

    private void setupMessage(String subject) throws MessagingException {
        message.setSender(new InternetAddress(username));
        message.setSubject(subject);
        multipart = new MimeMultipart();
    }

    public void addAttachment(File recordFile) throws MessagingException {
        // Recording bodyPart
        DataSource source = new FileDataSource(recordFile);
        BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setDataHandler(new DataHandler(source));
        messageBodyPart.setFileName(recordFile.getName());
        multipart.addBodyPart(messageBodyPart);
    }

    public void addLocation(String locationAddress) throws MessagingException {
        // Location bodyPart
        BodyPart messageTextBodyPart = new MimeBodyPart();
        messageTextBodyPart.setContent(locationAddress, "text/plain; charset=UTF-8");
        multipart.addBodyPart(messageTextBodyPart);
    }

    public void addRecipient(String destinatario) throws MessagingException {
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(destinatario));
    }

    public void sendMail() throws MessagingException {
        message.setContent(multipart);
        Transport.send(message);
    }
}

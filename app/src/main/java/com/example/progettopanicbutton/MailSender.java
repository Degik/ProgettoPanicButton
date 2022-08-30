package com.example.progettopanicbutton;

import android.net.Uri;

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
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.activation.DataSource;
import javax.mail.internet.MimeMultipart;

public class MailSender {
    private final String username = "mailtestdvb@gmail.com";
    private final String password = "isa990021";
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

    private void setupMessage(String subject) throws MessagingException {
        message.setFrom(new InternetAddress(username));
        message.setSubject(subject);
        multipart = new MimeMultipart();
    }

    public void addAttachment(String filename) throws MessagingException {
        DataSource source = new FileDataSource(filename);
        BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setDataHandler(new DataHandler(source));
        messageBodyPart.setFileName(filename);
        multipart.addBodyPart(messageBodyPart);
    }

    public void addLocation(String locationAddress) throws MessagingException {
        message.setText(locationAddress);
    }

    public void addRecipient(String destinatario) throws MessagingException {
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(destinatario));
    }

    public void sendMail() throws MessagingException {
        Transport.send(message);
    }
}

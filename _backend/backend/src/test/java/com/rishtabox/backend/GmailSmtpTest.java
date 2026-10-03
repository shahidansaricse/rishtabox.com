package com.rishtabox.backend;

import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;

import java.util.Properties;

public class GmailSmtpTest {

    public static void main(String[] args) {

        String username = "mdshahidans2005@gmail.com";

        // Yahan NEW App Password daalo
        String password = "dkpvnzcdbgwenjwo";

        Properties props = new Properties();

        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.put("mail.smtp.ssl.checkserveridentity", "true");
        props.put("mail.smtp.auth.mechanisms", "LOGIN");

        Session session = Session.getInstance(
                props,
                new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(
                                username,
                                password
                        );
                    }
                }
        );

        session.setDebug(true);

        try {
            Transport transport = session.getTransport("smtp");

            transport.connect(
                    "smtp.gmail.com",
                    587,
                    username,
                    password
            );

            System.out.println();
            System.out.println("=================================");
            System.out.println("SMTP AUTH SUCCESS");
            System.out.println("Gmail accepted the App Password");
            System.out.println("=================================");

            transport.close();

        } catch (Exception e) {

            System.out.println();
            System.out.println("=================================");
            System.out.println("SMTP AUTH FAILED");
            System.out.println("=================================");

            e.printStackTrace();
        }
    }
}
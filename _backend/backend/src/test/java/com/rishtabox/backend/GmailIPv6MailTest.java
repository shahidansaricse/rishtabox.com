package com.rishtabox.backend;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

public class GmailIPv6MailTest {

    public static void main(String[] args) {

        String username = "mdshahidans2005@gmail.com";
        String password = "tkpqzguypexgfyxz";

        System.out.println("=================================");
        System.out.println("GMAIL IPv6 MAIL TEST");
        System.out.println("=================================");

        System.setProperty("java.net.preferIPv6Addresses", "true");
        System.setProperty("java.net.preferIPv4Stack", "false");

        Properties props = new Properties();

        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");

        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");

        props.put("mail.smtp.connectiontimeout", "15000");
        props.put("mail.smtp.timeout", "15000");
        props.put("mail.smtp.writetimeout", "15000");

        props.put("mail.debug", "true");

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

        try {

            System.out.println("Connecting to Gmail...");

            Message message = new MimeMessage(session);

            message.setFrom(
                    new InternetAddress(username)
            );

            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(username)
            );

            message.setSubject("RishtaBox Gmail Test");

            message.setText(
                    "This is a test email from RishtaBox."
            );

            Transport.send(message);

            System.out.println();
            System.out.println("=================================");
            System.out.println("EMAIL SENT SUCCESSFULLY");
            System.out.println("=================================");

        } catch (Exception e) {

            System.out.println();
            System.out.println("=================================");
            System.out.println("EMAIL FAILED");
            System.out.println("=================================");

            e.printStackTrace();
        }
    }
}
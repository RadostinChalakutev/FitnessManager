package com.fitness.service;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

public class EmailService {

    private final String senderEmail =
            "YOUR_EMAIL@gmail.com";

    private final String senderPassword =
            "YOUR_APP_PASSWORD";

    public void sendVerificationEmail(
            String recipientEmail,
            String recipientName,
            String verificationToken) {

        String verificationLink =
                "http://localhost:8080/verify?token="
                        + verificationToken;

        Properties properties = new Properties();

        properties.put(
                "mail.smtp.auth",
                "true"
        );

        properties.put(
                "mail.smtp.starttls.enable",
                "true"
        );

        properties.put(
                "mail.smtp.host",
                "smtp.gmail.com"
        );

        properties.put(
                "mail.smtp.port",
                "587"
        );

        Session session =
                Session.getInstance(
                        properties,
                        new Authenticator() {

                            @Override
                            protected PasswordAuthentication
                            getPasswordAuthentication() {

                                return new PasswordAuthentication(
                                        senderEmail,
                                        senderPassword
                                );
                            }
                        }
                );

        try {

            Message message =
                    new MimeMessage(session);

            message.setFrom(
                    new InternetAddress(senderEmail)
            );

            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(
                            recipientEmail
                    )
            );

            message.setSubject(
                    "Fitness Manager - Verify your email"
            );

            message.setText(
                    "Hello "
                            + recipientName
                            + ",\n\n"
                            + "Please verify your email address "
                            + "by clicking the link below:\n\n"
                            + verificationLink
                            + "\n\n"
                            + "If you did not register at "
                            + "Fitness Manager, you can ignore "
                            + "this email."
            );

            Transport.send(message);

            System.out.println(
                    "Verification email sent to: "
                            + recipientEmail
            );

        } catch (MessagingException e) {

            System.out.println(
                    "Failed to send verification email."
            );

            e.printStackTrace();
        }
    }
}
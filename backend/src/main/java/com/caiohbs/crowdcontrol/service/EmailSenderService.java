package com.caiohbs.crowdcontrol.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

@Service
public class EmailSenderService {

    @Value("${spring.mail.username}")
    private String appEmail;
    @Value("${crowdcontrol.vars.WEBSITE_ADDRESS}")
    private String baseAddress;
    private final JavaMailSender mailSender;


    public EmailSenderService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Sends an email to the specified recipient with a subject and body determined
     * by the email type.
     *
     * @param to   The recipient's email address.
     * @param type The type of email to send ({@code ENABLE_ACC} or {@code RECOV_PASS}).
     * @param code A verification code to be included in the email body.
     */
    public void sendEmail(String to, String type, String code) {

        String enableSubject = "CrowdControl || Enable your account";
        String recovSubject = "CrowdControl || Password reset";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(appEmail);
        message.setTo(to);

        message.setSubject(Objects.equals(type, "ENABLE_ACC") ? enableSubject : recovSubject);

        message.setText(getEmailText(type, code));

        this.mailSender.send(message);

    }

    /**
     * Retrieves the email body content based on the specified email type and
     * replaces placeholders with provided data.
     *
     * @param type The type of email ({@code ENABLE_ACC} or {@code RECOV_PASS}).
     * @param code The verification code to be included in the email body.
     * @return The formatted email body content.
     * @throws RuntimeException If any errors occur while reading the email
     *                          template file.
     */
    private String getEmailText(String type, String code) throws RuntimeException {

        String basePath = "src/main/resources/templates/emails/";

        if (type.equals("ENABLE_ACC")) {
            basePath += "enable-account.txt";
        } else {
            basePath += "reset-password.txt";
        }

        try {
            String fullPath = new String(Files.readAllBytes(Paths.get(basePath)));

            if (type.equals("ENABLE_ACC")) {
                return fullPath.replace(
                        "[LINK]", baseAddress + "/enable-acc?code=" + code
                );
            } else {
                return fullPath.replace(
                        "[LINK]", baseAddress + "/reset-pass?code=" + code
                );
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}

package com.App.GetYourGuide.Service;


import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleMailMessage.class);

    public void sendEmail(String receiverEmail, String subject, String message) {

        LOGGER.info("Starting email creation...");

        try {
            SimpleMailMessage mailMessage = createMail(receiverEmail, subject, message);

            javaMailSender.send(mailMessage);

            LOGGER.info("Email has been sent.");

        } catch (MailException e) {
            LOGGER.info("Failed to process email sending: " + e.getMessage(), e);
        }
    }

    private SimpleMailMessage createMail(String receiverEmail, String subject, String message){
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(receiverEmail);
        mailMessage.setSubject(receiverEmail);
        mailMessage.setText(message);
        return mailMessage;
    }
}

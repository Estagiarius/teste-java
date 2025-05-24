package com.teacheragenda.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender javaMailSender;

    @Value("${teacher.agenda.notification.email}")
    private String recipientEmail;

    @Value("${spring.mail.from}")
    private String senderEmail;

    @Autowired
    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public boolean sendNotification(String subject, String textBody) {
        if (recipientEmail == null || recipientEmail.isEmpty() || recipientEmail.equals("teacher@example.com")) {
            logger.warn("Recipient email is not configured or is set to the default placeholder. Skipping email send.");
            // Optionally, inform the user via UI if this happens interactively
            return false;
        }
        if (senderEmail == null || senderEmail.isEmpty() || senderEmail.equals("user@example.com") || "smtp.example.com".equals(javaMailSender.getJavaMailProperties().getProperty("mail.smtp.host")) ) {
            logger.warn("Sender email or SMTP host is not configured or is set to the default placeholder. Skipping email send.");
            return false;
        }


        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderEmail);
        message.setTo(recipientEmail);
        message.setSubject(subject);
        message.setText(textBody);

        try {
            javaMailSender.send(message);
            logger.info("Notification email sent successfully to {} with subject: {}", recipientEmail, subject);
            return true;
        } catch (MailException e) {
            logger.error("Failed to send notification email to {}: {}", recipientEmail, e.getMessage(), e);
            // Consider more specific error handling or re-throwing a custom exception
            return false;
        }
    }
}

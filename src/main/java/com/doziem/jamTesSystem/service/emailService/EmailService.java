package com.doziem.jamTesSystem.service.emailService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${pharmacy.warning.email.to:admin@localhost}")
    private String recipient;

    @Value("${app.email.from:no-reply@localhost}")
    private String fromAddress;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendLowStockWarning(String pharmacyName, String department, String medicationName, int quantityInStock) {
        if (mailSender == null || !StringUtils.hasText(recipient) || "admin@localhost".equals(recipient)) {
            log.warn("Email warning not sent because no SMTP recipient is configured. Pharmacy: {}, Department: {}, Medication: {}, Quantity: {}",
                    pharmacyName, department, medicationName, quantityInStock);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(recipient);
            message.setSubject("Pharmacy stock warning");
            message.setText("Pharmacy stock alert\n\n"
                    + "Pharmacy: " + pharmacyName + "\n"
                    + "Department: " + department + "\n"
                    + "Medication: " + medicationName + "\n"
                    + "Available quantity: " + quantityInStock + "\n"
                    + "Action required: please reorder or transfer stock immediately.");
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Failed to send low stock warning email for {} in {}: {}", medicationName, pharmacyName, e.getMessage(), e);
        }
    }

    public void sendVerificationEmail(String email, String fullName, String token) {
        if (mailSender == null || !StringUtils.hasText(email)) {
            log.warn("Verification email not sent because recipient email is missing. Email: {}", email);
            return;
        }

        try {
            String verificationLink = "http://localhost:8080/auth/verify-email?email=" + email + "&token=" + token;
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(email);
            message.setSubject("Verify your email address");
            message.setText("Hello " + fullName + ",\n\n"
                    + "Thank you for registering. Please verify your email by clicking the link below:\n"
                    + verificationLink + "\n\n"
                    + "If you did not create this account, you can ignore this email.");
            mailSender.send(message);
            log.info("Verification email sent to {}", email);
        } catch (Exception e) {
            log.error("Failed to send verification email to {}: {}", email, e.getMessage(), e);
        }
    }

    public void sendVerificationReminder(String email, String fullName) {
        if (mailSender == null || !StringUtils.hasText(email)) {
            log.warn("Verification reminder email not sent because recipient email is missing. Email: {}", email);
            return;
        }

        try {
            String verificationLink = "http://localhost:8080/auth/verify-email?email=" + email;
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(email);
            message.setSubject("Verify your email to continue login");
            message.setText("Hello " + fullName + ",\n\n"
                    + "Your email is not verified yet. Please verify it before logging in.\n"
                    + verificationLink + "\n\n"
                    + "This reminder was sent because login was attempted without verification.");
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Failed to send verification reminder to {}: {}", email, e.getMessage(), e);
        }
    }
}

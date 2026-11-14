package com.doziem.jamTesSystem.service.emailService;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${pharmacy.warning.email.to:admin@localhost}")
    private String recipient;

    @Value("${app.email.from:no-reply@localhost}")
    private String fromAddress;

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
        if (!StringUtils.hasText(email)) {
            throw new IllegalArgumentException("Recipient email is required for verification email.");
        }
        if (!StringUtils.hasText(token)) {
            throw new IllegalArgumentException("Verification token is required.");
        }
        SimpleMailMessage message = getVerificationMessage(email, fullName, token, "Verify your email address",
                "Thank you for registering. Please verify your email by clicking the link below:");
        mailSender.send(message);
        log.info("Verification email sent to {}", email);
    }

    private SimpleMailMessage getVerificationMessage(String email, String fullName, String token, String subject, String bodyIntro) {
        String verificationLink = "http://localhost:8080/auth/verify-email?email=" + email + "&token=" + token;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(email);
        message.setSubject(subject);
        message.setText("Hello " + fullName + ",\n\n"
                + bodyIntro + "\n"
                + verificationLink + "\n\n"
                + "If you did not create this account, you can ignore this email.");
        return message;
    }

    public void sendVerificationReminder(String email, String fullName, String token) {
        if (!StringUtils.hasText(email)) {
            throw new IllegalArgumentException("Recipient email is required for verification reminder.");
        }
        if (!StringUtils.hasText(token)) {
            throw new IllegalArgumentException("Verification token is required.");
        }
        SimpleMailMessage message = getVerificationMessage(email, fullName, token, "Verify your email to continue login",
                "Your email is not verified yet. Please verify it before logging in:");
        mailSender.send(message);
        log.info("Verification reminder email sent to {}", email);
    }
}

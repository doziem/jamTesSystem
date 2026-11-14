package com.doziem.jamTesSystem.config;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmailUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailUtil.class);

    private JavaMailSender mailSender;

    public void sendVerificationMail(String to, String subject, String text) {
        try {
            var message = mailSender.createMimeMessage();
            var helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true); // Set to true for HTML content
            mailSender.send(message);
        } catch (Exception e) {
            LOGGER.error("Failed to send verification email to {}: {}", to, e.getMessage(), e);
        }
    }
}

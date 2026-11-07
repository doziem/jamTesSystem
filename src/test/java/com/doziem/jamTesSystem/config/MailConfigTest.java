package com.doziem.jamTesSystem.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import static org.assertj.core.api.Assertions.assertThat;

class MailConfigTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(MailConfig.class);

    @Test
    void bindsMailPropertiesToSender() {
        contextRunner.withPropertyValues(
                "spring.mail.host=mail.example.test",
                "spring.mail.port=1025",
                "spring.mail.username=test-user",
                "spring.mail.properties.mail.smtp.auth=true")
                .run(context -> {
                    assertThat(context).hasNotFailed().hasSingleBean(JavaMailSender.class);
                    JavaMailSenderImpl sender = context.getBean(JavaMailSenderImpl.class);
                    assertThat(sender.getHost()).isEqualTo("mail.example.test");
                    assertThat(sender.getPort()).isEqualTo(1025);
                    assertThat(sender.getUsername()).isEqualTo("test-user");
                    assertThat(sender.getJavaMailProperties()).containsEntry("mail.smtp.auth", "true");
                });
    }

    @Test
    void startsWhenPortIsNotConfigured() {
        contextRunner.withPropertyValues("spring.mail.host=localhost")
                .run(context -> {
                    assertThat(context).hasNotFailed().hasSingleBean(JavaMailSender.class);
                    assertThat(context.getBean(JavaMailSenderImpl.class).getPort()).isEqualTo(-1);
                });
    }
}

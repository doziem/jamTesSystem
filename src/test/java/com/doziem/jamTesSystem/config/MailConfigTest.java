package com.doziem.jamTesSystem.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mock.env.MockEnvironment;

import java.io.IOException;

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

    @Test
    void emailAddressesUseDefaultsWhenMailUsernameIsMissing() throws IOException {
        MockEnvironment environment = applicationEnvironment();

        assertThat(environment.getProperty("app.email.from")).isEqualTo("no-reply@localhost");
        assertThat(environment.getProperty("pharmacy.warning.email.to")).isEqualTo("admin@localhost");
    }

    @Test
    void emailAddressesUseConfiguredMailUsername() throws IOException {
        MockEnvironment environment = applicationEnvironment().withProperty("MAIL_USERNAME", "sender@example.com");

        assertThat(environment.getProperty("app.email.from")).isEqualTo("sender@example.com");
        assertThat(environment.getProperty("pharmacy.warning.email.to")).isEqualTo("sender@example.com");
    }

    private MockEnvironment applicationEnvironment() throws IOException {
        MockEnvironment environment = new MockEnvironment();
        environment.getPropertySources().addLast(new PropertiesPropertySource("application",
                PropertiesLoaderUtils.loadProperties(new ClassPathResource("application.properties"))));
        return environment;
    }
}

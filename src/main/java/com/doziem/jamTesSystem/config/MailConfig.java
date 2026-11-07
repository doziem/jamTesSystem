package com.doziem.jamTesSystem.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

//@Configuration
//public class MailConfig {
//
//    @Bean
//    @ConditionalOnMissingBean(JavaMailSender.class)
//    public JavaMailSender javaMailSender() {
//        JavaMailSenderImpl sender = new JavaMailSenderImpl();
//        sender.setHost("localhost");
//        sender.setPort(25);
//        sender.setDefaultEncoding("UTF-8");
//
//        Properties properties = new Properties();
//        properties.put("mail.smtp.auth", "false");
//        properties.put("mail.smtp.starttls.enable", "false");
//        sender.setJavaMailProperties(properties);
//        return sender;
//    }
//}


@Configuration
@EnableConfigurationProperties(MailProperties.class)
public class MailConfig {

    @Bean
    @ConditionalOnMissingBean(JavaMailSender.class)
    public JavaMailSender javaMailSender(MailProperties props) {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(props.getHost());
        if (props.getPort() != null) {
            sender.setPort(props.getPort());
        }
        sender.setUsername(props.getUsername());
        sender.setPassword(props.getPassword());
        sender.setDefaultEncoding("UTF-8");

        Properties javaMailProps = sender.getJavaMailProperties();
        javaMailProps.putAll(props.getProperties());
        return sender;
    }
}

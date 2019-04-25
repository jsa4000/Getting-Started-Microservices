package com.example.notifier.properties;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@NoArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "mail.smtp")
public class SmtpProperties {

    private boolean auth;
    private boolean starttlsEnabled;
    private String host;
    private String port;
    private String ssltrust;

}
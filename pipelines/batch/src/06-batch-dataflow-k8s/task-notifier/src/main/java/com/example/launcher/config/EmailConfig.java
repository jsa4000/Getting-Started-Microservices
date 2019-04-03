package com.example.launcher.config;

import com.example.launcher.services.EmailService;
import com.example.launcher.services.ExchangeEmailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmailConfig {

    @Bean
    public EmailService emailService() {
        return new ExchangeEmailService();
    }

}

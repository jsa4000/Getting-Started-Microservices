package com.example.notifier.config;

import com.example.notifier.services.EmailService;
import com.example.notifier.services.ExchangeEmailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmailConfig {

    @Bean
    public EmailService emailService() {
        return new ExchangeEmailService();
    }

}

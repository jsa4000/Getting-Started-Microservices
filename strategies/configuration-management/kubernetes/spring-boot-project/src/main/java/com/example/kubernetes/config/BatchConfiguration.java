package com.example.kubernetes.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.example.kubernetes.config.DataBaseProperties.DEFAULT_DATABASE_PROPERTIES;

@Configuration
public class BatchConfiguration {

    @Bean
    @ConfigurationProperties(DEFAULT_DATABASE_PROPERTIES)
    public DataBaseProperties databaseProperties() { return new DataBaseProperties(); }
}

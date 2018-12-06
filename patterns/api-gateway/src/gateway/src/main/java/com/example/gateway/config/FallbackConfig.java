package com.example.gateway.config;

import com.example.gateway.fallback.CustomerFallbackProvider;
import com.example.gateway.fallback.DefaultFallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FallbackConfig {

    @Bean
    public CustomerFallbackProvider customerFallbackProvider() {return new CustomerFallbackProvider(); }

    @Bean
    public DefaultFallbackProvider defaultFallbackProvider() { return new DefaultFallbackProvider(); }

}

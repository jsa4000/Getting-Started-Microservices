package com.tracing.tracingLib.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceDiscoveryConfiguration {

    private final Logger logger = LoggerFactory.getLogger(ServiceDiscoveryConfiguration.class);

    @Bean
    public ServiceDiscovery getServiceDiscovery() {
        return new ServiceDiscovery();
    }

}

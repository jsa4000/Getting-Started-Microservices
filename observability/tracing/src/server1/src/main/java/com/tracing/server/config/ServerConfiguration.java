package com.tracing.server.config;

import com.tracing.tracingLib.config.ServiceDiscoveryConfiguration;
import com.tracing.tracingLib.config.TracingConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;

@Configuration
@Import({TracingConfiguration.class, ServiceDiscoveryConfiguration.class})
public class ServerConfiguration {

     @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder.build();
    }

}

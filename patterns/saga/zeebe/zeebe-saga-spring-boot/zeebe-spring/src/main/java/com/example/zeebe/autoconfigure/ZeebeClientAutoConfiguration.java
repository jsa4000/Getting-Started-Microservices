package com.example.zeebe.autoconfigure;

import com.example.zeebe.aop.ZeebeWorkerAspect;
import io.zeebe.spring.client.EnableZeebeClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableZeebeClient
public class ZeebeClientAutoConfiguration {

    @Bean
    public ZeebeWorkerAspect zeebeTaskAspect() {
        return new ZeebeWorkerAspect();
    }
}

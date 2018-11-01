package com.example.devices.config;

import com.example.devices.mqtt.handlers.ResponseHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DevicesConfig {

    @Bean
    ResponseHandler responseHandler() { return new ResponseHandler(); }

}

package com.tracing.server.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class ServerController {

    private final Logger logger = LoggerFactory.getLogger(ServerController.class);

    @Value( "${service.name}" )
    private String serviceName;

    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping("/status")
    public String status() {
        logger.info(String.format("%s is UP", serviceName));
        return String.format("%s is UP", serviceName);
    }
}
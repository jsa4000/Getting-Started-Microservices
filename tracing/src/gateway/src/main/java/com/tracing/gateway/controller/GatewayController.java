package com.tracing.gateway.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class GatewayController {

    private final Logger logger = LoggerFactory.getLogger(GatewayController.class);

    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping("/hello")
    public String hello() {
        logger.info("hello call endpoint has benn called");
        return "Hello from Spring Boot!";
    }

    @RequestMapping("/chaining")
    public String chaining() {
        logger.info("Start calling to Chaining endpoint");
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:8080/hello", String.class);
        logger.info("End calling to Chaining endpoint");
        return "Chaining + " + response.getBody();
    }
}
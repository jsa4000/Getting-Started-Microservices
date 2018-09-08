package com.tracing.server1.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class ServerController {

    private final Logger logger = LoggerFactory.getLogger(ServerController.class);

    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping("/status")
    public String status() {
        logger.info(String.format("Server %s is UP",this.getClass().getSimpleName()));
        return String.format("Server %s is UP", this.getClass().getSimpleName());
    }

    @RequestMapping("/status/chain")
    public String statusChain() {
        logger.info("Start Call to Status endpoint");
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:8080/status", String.class);
        logger.info("End Call to Status endpoint");
        return "Chaining + " + response.getBody();
    }
}
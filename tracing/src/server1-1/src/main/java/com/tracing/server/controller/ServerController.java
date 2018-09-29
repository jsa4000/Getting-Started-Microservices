package com.tracing.server.controller;

import com.tracing.tracingLib.config.ServiceDiscovery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class ServerController {

    private final Logger logger = LoggerFactory.getLogger(ServerController.class);

    @Value("${spring.application.name}")
    private String serviceName;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ServiceDiscovery serviceDiscovery;

    @RequestMapping("/status")
    public String status() {
        logger.info(String.format("%s is UP", serviceName));
        return String.format("%s is UP", serviceName);
    }

    @RequestMapping("/server2/status")
    public String server2Status() {
        logger.info("Start Call to Server1 endpoint");
        ResponseEntity<String> response = restTemplate.getForEntity("http://" + serviceDiscovery.getServer2Address() +"/status", String.class);
        logger.info("End Call to Server1 endpoint");
        return "Server Status: " + response.getBody();
    }

    @RequestMapping("/customer")
    public String customer() {
        logger.info("Start Call to Server2 endpoint");
        ResponseEntity<String> response = restTemplate.getForEntity("http://" + serviceDiscovery.getServer2Address() +"/customer", String.class);
        logger.info("End Call to Server2 endpoint");
        return response.getBody();
    }
}
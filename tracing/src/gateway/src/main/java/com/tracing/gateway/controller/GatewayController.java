package com.tracing.gateway.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/")
public class GatewayController {

    private final Logger logger = LoggerFactory.getLogger(GatewayController.class);

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/status")
    public String status() {
        logger.info(String.format("Server %s is UP",this.getClass().getSimpleName()));
        return String.format("Server %s is UP", this.getClass().getSimpleName());
    }

    @GetMapping("/status/chain")
    public String statusChain() {
        logger.info("Start Call to Status endpoint");
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:8080/status", String.class);
        logger.info("End Call to Status endpoint");
        return "Chaining + " + response.getBody();
    }

    @GetMapping("/server1/status")
    public String server1Status() {
        logger.info("Start Call to Server1 endpoint");
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:8081/status", String.class);
        logger.info("End Call to Server1 endpoint");
        return "Server Status: " + response.getBody();
    }

    @GetMapping("/server1/server1-1/status")
    public String server11Status() {
        logger.info("Start Call to Server1 endpoint");
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:8081/server1-1/status", String.class);
        logger.info("End Call to Server1 endpoint");
        return "Server Status: " + response.getBody();
    }

    @GetMapping("/server1/server1-2/status")
    public String server12Status() {
        logger.info("Start Call to Server1 endpoint");
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:8081/server1-2/status", String.class);
        logger.info("End Call to Server1 endpoint");
        return "Server Status: " + response.getBody();
    }

    @GetMapping("/server1/server1-1/server2/status")
    public String server2Status() {
        logger.info("Start Call to Server1 endpoint");
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:8081/server1-1/server2/status", String.class);
        logger.info("End Call to Server1 endpoint");
        return "Server Status: " + response.getBody();
    }

    @GetMapping("/server2/status")
    public String server2DirectStatus() {
        logger.info("Start Call to Server1 endpoint");
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:8084/status", String.class);
        logger.info("End Call to Server1 endpoint");
        return "Server Status: " + response.getBody();
    }

    @GetMapping("/customer")
    public String customer() {
        logger.info("Start Call to Server1 endpoint");
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:8081/customer", String.class);
        logger.info("End Call to Server1 endpoint");
        return response.getBody();
    }

    @GetMapping("/scan")
    public String scan() {
        logger.info("Start Call to Server1 endpoint");
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:8081/scan", String.class);
        logger.info("End Call to Server1 endpoint");
        return response.getBody();
    }

    @GetMapping("/order")
    public String order() {
        logger.info("Start Call to Server1 endpoint");
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:8081/order", String.class);
        logger.info("End Call to Server1 endpoint");
        return response.getBody();
    }

    @GetMapping("/orderAsync")
    public String orderAsync() {
        logger.info("Start Call to Server1 endpoint");
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:8081/orderAsync", String.class);
        logger.info("End Call to Server1 endpoint");
        return response.getBody();
    }

}
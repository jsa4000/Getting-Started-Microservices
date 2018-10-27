package com.tracing.gateway.controller;

import com.tracing.tracingLib.config.ServiceDiscovery;
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

    @Autowired
    private ServiceDiscovery serviceDiscovery;

    @GetMapping("/status")
    public String status() {
        logger.info(String.format("Server %s is UP",this.getClass().getSimpleName()));
        return String.format("Server %s is UP", this.getClass().getSimpleName());
    }

    @GetMapping("/status/chain")
    public String statusChain() {
        logger.info("Start Call to Status endpoint");
        ResponseEntity<String> response = restTemplate.getForEntity("http://" + serviceDiscovery.getGatewayServerAddress() +"/status", String.class);
        logger.info("End Call to Status endpoint");
        return "Chaining + " + response.getBody();
    }

    @GetMapping("/server1/status")
    public String server1Status() {
        logger.info("Start Call to Server1 endpoint");
        ResponseEntity<String> response = restTemplate.getForEntity("http://" + serviceDiscovery.getServer1Address() +"/status", String.class);
        logger.info("End Call to Server1 endpoint");
        return "Server Status: " + response.getBody();
    }

    @GetMapping("/server1/server1-1/status")
    public String server11Status() {
        logger.info("Start Call to Server1 endpoint");
        ResponseEntity<String> response = restTemplate.getForEntity("http://" + serviceDiscovery.getServer1Address() +"/server1-1/status", String.class);
        logger.info("End Call to Server1 endpoint");
        return "Server Status: " + response.getBody();
    }

    @GetMapping("/server1/server1-2/status")
    public String server12Status() {
        logger.info("Start Call to Server1 endpoint");
        ResponseEntity<String> response = restTemplate.getForEntity("http://" + serviceDiscovery.getServer1Address() +"/server1-2/status", String.class);
        logger.info("End Call to Server1 endpoint");
        return "Server Status: " + response.getBody();
    }

    @GetMapping("/server1/server1-1/server2/status")
    public String server2Status() {
        logger.info("Start Call to Server1 endpoint");
        ResponseEntity<String> response = restTemplate.getForEntity("http://" + serviceDiscovery.getServer1Address() +"/server1-1/server2/status", String.class);
        logger.info("End Call to Server1 endpoint");
        return "Server Status: " + response.getBody();
    }

    @GetMapping("/server2/status")
    public String server2DirectStatus() {
        logger.info("Start Call to Server1 endpoint");
        ResponseEntity<String> response = restTemplate.getForEntity("http://" + serviceDiscovery.getServer2Address() +"/status", String.class);
        logger.info("End Call to Server1 endpoint");
        return "Server Status: " + response.getBody();
    }

    @GetMapping("/customer")
    public String customer() {
        logger.info("Start Call to Server1 endpoint");
        ResponseEntity<String> response = restTemplate.getForEntity("http://" + serviceDiscovery.getServer1Address() +"/customer", String.class);
        logger.info("End Call to Server1 endpoint");
        return response.getBody();
    }

    @GetMapping("/scan")
    public String scan() {
        logger.info("Start Call to Server1 endpoint");
        ResponseEntity<String> response = restTemplate.getForEntity("http://" + serviceDiscovery.getServer1Address() +"/scan", String.class);
        logger.info("End Call to Server1 endpoint");
        return response.getBody();
    }

    @GetMapping("/order")
    public String order() {
        logger.info("Start Call to Server1 endpoint");
        ResponseEntity<String> response = restTemplate.getForEntity("http://" + serviceDiscovery.getServer1Address() +"/order", String.class);
        logger.info("End Call to Server1 endpoint");
        return response.getBody();
    }

    @GetMapping("/orderAsync")
    public String orderAsync() {
        logger.info("Start Call to Server1 endpoint");
        ResponseEntity<String> response = restTemplate.getForEntity("http://" + serviceDiscovery.getServer1Address() +"/orderAsync", String.class);
        logger.info("End Call to Server1 endpoint");
        return response.getBody();
    }

}
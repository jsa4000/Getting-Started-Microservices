package com.tracing.server.controller;

import com.tracing.tracingLib.helpers.TracingPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@RestController
public class ServerController {

    private final Logger logger = LoggerFactory.getLogger(ServerController.class);
    private final int poolSize = 10;

    private final ExecutorService pool;

    @Value("${spring.application.name}")
    private String serviceName;

    @Autowired
    private RestTemplate restTemplate;

    public ServerController() {
        this.pool = TracingPool.createPool(poolSize);
    }

    @RequestMapping("/status")
    public String status() {
        logger.info(String.format("%s is UP", serviceName));
        return String.format("%s is UP", serviceName);
    }

    @RequestMapping("/server1-1/status")
    public String server11Status() {
        logger.info("Start Call to Server1 endpoint");
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:8082/status", String.class);
        logger.info("End Call to Server1 endpoint");
        return "Server Status: " + response.getBody();
    }

    @RequestMapping("/server1-2/status")
    public String server12Status() {
        logger.info("Start Call to Server1 endpoint");
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:8083/status", String.class);
        logger.info("End Call to Server1 endpoint");
        return "Server Status: " + response.getBody();
    }

    @RequestMapping("/server1-1/server2/status")
    public String server2Status() {
        logger.info("Start Call to Server1 endpoint");
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:8082/server2/status", String.class);
        logger.info("End Call to Server1 endpoint");
        return "Server Status: " + response.getBody();
    }

    @RequestMapping("/customer")
    public String customer() {
        logger.info("Start Call to Server1-1 endpoint");
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:8082/customer", String.class);
        logger.info("End Call to Server1-1 endpoint");
        return response.getBody();
    }

    public void sleep(Integer milliseconds)  {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/scan")
    public String scan() {

        TracingPool.executeSpan("check-services-connected", () -> sleep(200) );
        TracingPool.executeSpan("check-services-running", () -> sleep(200) );

        return "Scan Finished";
    }

    @RequestMapping("/order")
    public String order() {

        logger.info("Start Call to Server1-2 endpoint");
        ResponseEntity<String> responseOrder = restTemplate.getForEntity("http://localhost:8083/order", String.class);
        logger.info("End Call to Server1-2 endpoint");

        logger.info("Start Call to Server1-1 endpoint");
        ResponseEntity<String> responseCustomer = restTemplate.getForEntity("http://localhost:8082/customer", String.class);
        logger.info("End Call to Server1-1 endpoint");

        return String.format("order: %s - customer: %s",responseOrder.getBody(),responseCustomer.getBody());
    }

    @RequestMapping("/orderAsync")
    public String orderAsync() throws ExecutionException, InterruptedException {

        Future<ResponseEntity<String>> responseOrderAsync = pool.submit(() -> {
            logger.info("Start Call to Server1-2 endpoint");
            ResponseEntity<String> responseOrder = restTemplate.getForEntity("http://localhost:8083/order", String.class);
            logger.info("End Call to Server1-2 endpoint");
            return responseOrder;
                });
        Future<ResponseEntity<String>> responseCustomerAsync = pool.submit(() -> {
            logger.info("Start Call to Server1-1 endpoint");
            ResponseEntity<String> responseCustomer = restTemplate.getForEntity("http://localhost:8082/customer", String.class);
            logger.info("End Call to Server1-1 endpoint");
            return responseCustomer;
        });

        return String.format("order: %s - customer: %s",responseOrderAsync.get().getBody(),responseCustomerAsync.get().getBody());
    }
}
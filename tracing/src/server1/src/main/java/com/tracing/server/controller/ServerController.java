package com.tracing.server.controller;

import io.opentracing.Span;
import io.opentracing.contrib.concurrent.TracedExecutorService;
import io.opentracing.util.GlobalTracer;
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
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@RestController
public class ServerController {

    private final Logger logger = LoggerFactory.getLogger(ServerController.class);
    private final int poolSize = 10;

    private final ExecutorService pool;

    @Value( "${service.name}" )
    private String serviceName;

    @Autowired
    private RestTemplate restTemplate;

    public ServerController() {
        this.pool =  new TracedExecutorService(Executors.newFixedThreadPool(poolSize), GlobalTracer.get());
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

    @RequestMapping("/scan")
    public String scan() throws InterruptedException {
        Span checkServicesConnectedSpan = GlobalTracer.get()
                .buildSpan("check-services-connected")
                .asChildOf(GlobalTracer.get().activeSpan())
                .start();
        Thread.sleep(200);
        checkServicesConnectedSpan.finish();

        Span checkServicesRunningSpan = GlobalTracer.get()
                .buildSpan("check-services-running")
                .asChildOf(GlobalTracer.get().activeSpan())
                .start();
        Thread.sleep(200);
        checkServicesRunningSpan.finish();
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
package com.logging.gateway.controller;

import com.logging.gateway.model.Customer;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Random;

@RestController
@RequestMapping("/")
public class GatewayController {

    private final Logger logger = LoggerFactory.getLogger(GatewayController.class);

    private Random rand;

    private Counter statusCounter;
    private Counter customerCounter;
    private Counter orderCounter;
    private Counter orderErrorCounter;

    GatewayController()
    {
        rand = new Random();

        MeterRegistry registry = Metrics.globalRegistry;

        statusCounter = Counter
                .builder("com.logging.status.counter")
                .description("Indicates the number of total requests performed for status")
                .tags("dev", "performance")
                .tag("type", "request")
                .register(registry);

        customerCounter = Counter
                .builder("com.logging.customer.counter")
                .description("Indicates the number of total requests performed for customers")
                .tags("dev", "performance")
                .tag("type", "request")
                .register(registry);

        orderCounter = Counter
                .builder("com.logging.order.counter")
                .description("Indicates the number of total requests performed for orders")
                .tags("dev", "performance")
                .tag("type", "request")
                .register(registry);

        orderErrorCounter = Counter
                .builder("com.logging.order.error.counter")
                .description("Indicates the number of total requests errors for orders")
                .tags("dev", "performance")
                .tag("type", "request_error")
                .register(registry);
    }

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/status")
    public ResponseEntity<String> status() {
        statusCounter.increment(1.0);
        logger.info(String.format("Server %s is UP",this.getClass().getSimpleName()));
        return ResponseEntity.ok(String.format("Server %s is UP", this.getClass().getSimpleName()));
    }

    @GetMapping("/customer")
    @Produces(MediaType.APPLICATION_JSON)
    public ResponseEntity<Customer> customer() {
        customerCounter.increment(1.0);
        Customer customer = new Customer(1,"Javier", "Perez",35);
        logger.info(String.format("Customer ",customer.toString()));
        return ResponseEntity.ok(customer);
    }

    @GetMapping("/order")
    public ResponseEntity<String> order() throws Exception {
        try {
            orderCounter.increment(1.0);

            if (rand.nextInt(1000) % 2 == 0) {
                throw new Exception("Order Exception");
            }

            return ResponseEntity.ok("order123");
        }
        catch(Exception ex)
        {
            orderErrorCounter.increment(1.0);
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
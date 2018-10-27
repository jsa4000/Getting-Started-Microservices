package com.logging.gateway.controller;

import com.logging.gateway.model.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

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

    @GetMapping("/customer")
    @Produces(MediaType.APPLICATION_JSON)
    public Customer customer() {
        Customer customer = new Customer(1,"Javier", "Perez",35);
        logger.info(String.format("Customer ",customer.toString()));
        return customer;
    }

    @GetMapping("/error")
    public String error() throws Exception {
        throw new Exception("Customer Error");
    }
}
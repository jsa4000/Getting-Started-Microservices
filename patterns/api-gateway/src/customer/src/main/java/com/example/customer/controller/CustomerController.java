package com.example.customer.controller;

import com.example.customer.exceptions.CustomerNotFoundException;
import com.example.customer.model.Customer;
import com.example.customer.service.CustomerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Api(tags="Customers", description = "Customers View.")
@Controller
@RequestMapping("/customers")
public class CustomerController {

    @Autowired private CustomerService service;

    @ApiOperation(value = "Get a pageable list of customers")
    @GetMapping("/")
    ResponseEntity<PagedResources<Customer>> getCustomers(Pageable pageable, PagedResourcesAssembler assembler) {
        Page<Customer> customers = service.findAll(pageable);
        return new ResponseEntity<>(assembler.toResource(customers), HttpStatus.OK);
    }

    @ApiOperation(value = "Get a customer by Id")
    @GetMapping("/{id}")
    ResponseEntity<Customer> getCustomerById(@PathVariable String id) {
        Optional<Customer> customer =  service.getCustomerById(id);
        if (customer.isPresent())
            return new ResponseEntity<>(customer.get(), HttpStatus.OK);
        else
           throw new CustomerNotFoundException("Resource not found in the database");
    }

    @ApiOperation(value = "Create a new customer")
    @PostMapping("/")
    ResponseEntity<Customer> createCustomer(@RequestBody Customer customer) {
        Customer createdCustomer =  service.create(customer);
        return new ResponseEntity<>(createdCustomer, HttpStatus.OK);
    }

    @ApiOperation(value = "Delete an existing customer")
    @DeleteMapping("/{id}")
    ResponseEntity deleteCustomer(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.ok(StringUtils.EMPTY);
    }
}

